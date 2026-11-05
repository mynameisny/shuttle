package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.domain.repo.StopRepository;
import me.ningyu.app.shuttle.domain.repo.StopTimeRepository;
import me.ningyu.app.shuttle.model.stop.CreateStopRequest;
import me.ningyu.app.shuttle.model.stop.StopUsageSummaryView;
import me.ningyu.app.shuttle.model.stop.StopUsageView;
import me.ningyu.app.shuttle.model.stop.UpdateStopRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StopService
{
    private final StopRepository stopRepository;
    private final StopTimeRepository stopTimeRepository;

    /**
     * 创建停靠点（管理端地图页手工选点）。
     * stop_id 不传时自动生成（S + 12位大写随机串）。
     */
    @Nonnull
    public Stop createStop(CreateStopRequest request)
    {
        String stopId = request.getStopId() != null && !request.getStopId().isBlank()
                ? request.getStopId().trim()
                : generateStopId();
        if (stopRepository.existsByStopId(stopId))
        {
            throw new IllegalArgumentException("站点ID已存在: " + stopId);
        }

        Stop stop = Stop.builder()
                .stopId(stopId)
                .stopName(request.getStopName())
                .stopLat(request.getLat())
                .stopLon(request.getLng())
                .build();
        return stopRepository.save(stop);
    }

    /**
     * 更新停靠点名称与坐标（管理端站点维护页）。
     * stop_id 不允许修改（GTFS 主标识符，被 stop_times 引用）；
     * 坐标修正会同步影响所有引用该站点的班次。
     */
    @Nonnull
    public Stop updateStop(Long id, UpdateStopRequest request)
    {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("站点不存在: id=" + id));
        stop.setStopName(request.getStopName().trim());
        stop.setStopLat(request.getLat());
        stop.setStopLon(request.getLng());
        return stopRepository.save(stop);
    }

    /**
     * 删除停靠点（管理端站点维护页）。
     * 被 stop_times 引用的站点拒绝删除（GTFS 引用完整性），须先在班次中移除该站；
     * 无引用站点直接删除。
     */
    public void deleteStop(Long id)
    {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "站点不存在: id=" + id));
        int refs = stopTimeRepository.findByStopId(id).size();
        if (refs > 0)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "站点被 " + refs + " 条班次停站引用，不能删除；请先在班次中移除该站");
        }
        stopRepository.delete(stop);
    }

    /**
     * 查询停靠点列表，keyword 非空时按名称模糊匹配。
     */
    @Nonnull
    public List<Stop> getStops(String keyword)
    {
        if (keyword != null && !keyword.isBlank())
        {
            return stopRepository.findByStopNameContaining(keyword.trim());
        }
        return stopRepository.findAll();
    }

    /**
     * 反查引用某站点的班次及其所属线路（基于 stop_times）。
     * 按线路简称 + 方向 + 站序排序；用于站点维护页展示修改坐标的影响面，
     * 也是站点删除保护的基础查询。
     */
    @Nonnull
    public List<StopUsageView> getStopUsage(Long id)
    {
        stopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("站点不存在: id=" + id));
        return stopTimeRepository.findByStopId(id).stream()
                .map(StopUsageView::of)
                .sorted(Comparator
                        .comparing((StopUsageView v) -> v.getRouteShortName() == null ? "" : v.getRouteShortName())
                        .thenComparing(v -> v.getDirectionId() == null ? Integer.MAX_VALUE : v.getDirectionId())
                        .thenComparing(v -> v.getStopSequence() == null ? Integer.MAX_VALUE : v.getStopSequence()))
                .collect(Collectors.toList());
    }

    /**
     * 批量站点引用摘要：一次投影查询全部停站，按站点分组、线路+方向去重。
     * 用于站点台账列表项标注 [线路·方向] 角标（同名站消歧）与孤儿站点识别；
     * 无引用的站点不出现在结果中。
     */
    @Nonnull
    public List<StopUsageSummaryView> getUsageSummary()
    {
        Map<Long, Map<String, StopUsageSummaryView.Ref>> byStop = new LinkedHashMap<>();
        for (StopTimeRepository.StopRefProjection p : stopTimeRepository.findAllStopRefs())
        {
            byStop.computeIfAbsent(p.getStopPk(), k -> new LinkedHashMap<>())
                    .putIfAbsent(p.getRoutePk() + "_" + p.getDirectionId(),
                            StopUsageSummaryView.Ref.builder()
                                    .routePk(p.getRoutePk())
                                    .routeShortName(p.getRouteShortName())
                                    .directionId(p.getDirectionId())
                                    .build());
        }
        return byStop.entrySet().stream()
                .map(e -> StopUsageSummaryView.builder()
                        .stopPk(e.getKey())
                        .refs(List.copyOf(e.getValue().values()))
                        .build())
                .collect(Collectors.toList());
    }

    private String generateStopId()
    {
        return "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
    }
}
