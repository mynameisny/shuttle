package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Shape;
import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.domain.repo.ShapeRepository;
import me.ningyu.app.shuttle.domain.repo.StopTimeRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.model.shape.SaveShapeRequest;
import me.ningyu.app.shuttle.model.shape.ShapePointRequest;
import me.ningyu.app.shuttle.model.shape.TripShapeView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ShapeService
{
    /**
     * 地球平均半径（米），用于 haversine 球面距离累计。
     */
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    private final ShapeRepository shapeRepository;
    private final TripRepository tripRepository;
    private final StopTimeRepository stopTimeRepository;

    /**
     * 查询班次的形状与停站站点（管理端地图页渲染用）。
     */
    @Nonnull
    public TripShapeView getTripShape(Long tripPk)
    {
        Trip trip = tripRepository.findById(tripPk)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + tripPk));
        return buildTripShapeView(trip);
    }

    /**
     * 按形状标识查询点列（按 shape_pt_sequence 升序）。
     */
    @Nonnull
    public List<Shape> getShapePoints(String shapeId)
    {
        return shapeRepository.findByShapeIdOrderByShapePtSequenceAsc(shapeId);
    }

    /**
     * 保存班次的形状（管理端地图页描线结果）：
     * 全量替换目标 shape_id 的点列，按提交顺序自动编号 shape_pt_sequence，
     * 以 haversine 公式累计 shape_dist_traveled（单位：米），并将形状绑定到班次。
     */
    @Nonnull
    public TripShapeView saveTripShape(Long tripPk, SaveShapeRequest request)
    {
        Trip trip = tripRepository.findById(tripPk)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + tripPk));

        String shapeId = resolveShapeId(trip, request.getShapeId());

        // 绑定到班次（先更新再删旧点，避免 clearAutomatically 清缓存导致实体脱管）
        if (!shapeId.equals(trip.getShapeId()))
        {
            trip.setShapeId(shapeId);
            tripRepository.save(trip);
        }

        // 全量替换：删除该形状的全部旧点
        shapeRepository.deleteByShapeId(shapeId);

        // 重新编号并累计里程
        List<Shape> points = new ArrayList<>(request.getPoints().size());
        double accumulated = 0.0;
        ShapePointRequest prev = null;
        int sequence = 1;
        for (ShapePointRequest p : request.getPoints())
        {
            if (prev != null)
            {
                accumulated += haversine(
                        prev.getLat().doubleValue(), prev.getLng().doubleValue(),
                        p.getLat().doubleValue(), p.getLng().doubleValue());
            }
            points.add(Shape.builder()
                    .shapeId(shapeId)
                    .shapePtLat(p.getLat())
                    .shapePtLon(p.getLng())
                    .shapePtSequence(sequence++)
                    .shapeDistTraveled(BigDecimal.valueOf(accumulated).setScale(2, RoundingMode.HALF_UP))
                    .build());
            prev = p;
        }
        shapeRepository.saveAll(points);

        log.info("班次 {} 保存形状 {}：{} 点，总里程 {} 米",
                trip.getTripId(), shapeId, points.size(),
                points.get(points.size() - 1).getShapeDistTraveled());

        return buildTripShapeView(trip);
    }

    /**
     * 删除班次绑定的形状：删除该形状全部点列，并解除 trip.shape_id 绑定（幂等）。
     */
    public void removeTripShape(Long tripPk)
    {
        Trip trip = tripRepository.findById(tripPk)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + tripPk));
        if (trip.getShapeId() == null || trip.getShapeId().isBlank())
        {
            return;
        }
        // 先解绑再删点（与 saveTripShape 顺序一致，避免 clearAutomatically 清缓存后实体脱管）
        String shapeId = trip.getShapeId();
        trip.setShapeId(null);
        tripRepository.save(trip);
        shapeRepository.deleteByShapeId(shapeId);
        log.info("班次 {} 删除形状 {}（解绑）", trip.getTripId(), shapeId);
    }

    /**
     * 形状标识决策：请求指定 > 复用班次既有（编辑语义） > 自动生成。
     * 自动生成规则：{route_gtfs_id}_{outbound|inbound}_m{4位随机hex}。
     */
    private String resolveShapeId(Trip trip, String requested)
    {
        if (requested != null && !requested.isBlank())
        {
            return requested.trim();
        }
        if (trip.getShapeId() != null && !trip.getShapeId().isBlank())
        {
            return trip.getShapeId();
        }
        String direction = trip.getDirectionId() != null && trip.getDirectionId() == 1 ? "inbound" : "outbound";
        String routeGtfsId = trip.getRoute() != null ? trip.getRoute().getRouteId() : "route";
        String suffix = String.format(Locale.ROOT, "%04x", ThreadLocalRandom.current().nextInt(0x10000));
        return String.format(Locale.ROOT, "%s_%s_m%s", routeGtfsId, direction, suffix);
    }

    private TripShapeView buildTripShapeView(Trip trip)
    {
        List<Shape> points = trip.getShapeId() == null
                ? List.of()
                : shapeRepository.findByShapeIdOrderByShapePtSequenceAsc(trip.getShapeId());
        List<StopTime> stopTimes = stopTimeRepository.findByTripOrderByStopSequenceAsc(trip);
        return TripShapeView.of(trip, points, stopTimes);
    }

    /**
     * Haversine 公式计算两坐标点球面距离（米）。
     */
    private static double haversine(double lat1, double lon1, double lat2, double lon2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.sqrt(a));
    }
}
