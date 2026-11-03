package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.domain.repo.StopRepository;
import me.ningyu.app.shuttle.model.stop.CreateStopRequest;
import me.ningyu.app.shuttle.model.stop.UpdateStopRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StopService
{
    private final StopRepository stopRepository;

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

    private String generateStopId()
    {
        return "S" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT);
    }
}
