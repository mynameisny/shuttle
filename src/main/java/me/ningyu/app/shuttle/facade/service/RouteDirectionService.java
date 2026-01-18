package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Route;
import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import me.ningyu.app.shuttle.domain.repo.RouteDirectionRepository;
import me.ningyu.app.shuttle.domain.repo.RouteDirectionStopRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.ShuttleScheduleRepository;
import me.ningyu.app.shuttle.model.route.CreateRouteDirectionRequest;
import me.ningyu.app.shuttle.model.route.UpdateRouteDirectionRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RouteDirectionService
{
    private final RouteDirectionRepository routeDirectionRepository;
    private final RouteRepository routeRepository;
    private final RouteDirectionStopRepository routeDirectionStopRepository;
    private final ShuttleScheduleRepository scheduleRepository;


    /**
     * 创建带方向的线路
     */
    @NonNull
    public RouteDirection createRouteDirection(CreateRouteDirectionRequest request)
    {
        // 验证线路编码唯一性
        if (routeDirectionRepository.existsByCode(request.getCode()))
        {
            throw new IllegalArgumentException("线路编码已存在");
        }

        // 验证关联的线路是否存在
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("关联的线路不存在"));

        RouteDirection routeDirection = RouteDirection.builder()
                .code(request.getCode())
                .direction(request.getDirection())
                .description(request.getDescription())
                .route(route)
                .build();

        return routeDirectionRepository.save(routeDirection);
    }

    /**
     * 更新带方向的线路
     */
    @NonNull
    public RouteDirection updateRouteDirection(Long id, UpdateRouteDirectionRequest request)
    {
        RouteDirection routeDirection = routeDirectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("带方向的线路不存在"));

        // 如果编码发生变化，验证新编码的唯一性
        if (!routeDirection.getCode().equals(request.getCode()))
        {
            if (routeDirectionRepository.existsByCode(request.getCode()))
            {
                throw new IllegalArgumentException("线路编码已存在");
            }
        }

        // 验证关联的线路是否存在
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("关联的线路不存在"));

        routeDirection.setCode(request.getCode());
        routeDirection.setDirection(request.getDirection());
        routeDirection.setDescription(request.getDescription());
        routeDirection.setRoute(route);

        return routeDirectionRepository.save(routeDirection);
    }

    /**
     * 删除带方向的线路
     * 如果有关联的站点或排班，不允许删除
     */
    public void deleteRouteDirection(Long id)
    {
        RouteDirection routeDirection = routeDirectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("带方向的线路不存在"));

        // 检查是否有关联的站点
        if (routeDirectionStopRepository.existsByDirectionalRouteId(id))
        {
            throw new IllegalStateException("该线路方向下存在站点，不能删除");
        }

        // 检查是否有关联的排班
        if (scheduleRepository.existsByRouteDirectionId(id))
        {
            throw new IllegalStateException("该线路方向存在排班记录，不能删除");
        }

        routeDirectionRepository.delete(routeDirection);
    }

    /**
     * 获取带方向线路详情
     */
    @NonNull
    public RouteDirection getRouteDirectionById(Long id)
    {
        return routeDirectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("带方向的线路不存在"));
    }

    /**
     * 获取所有带方向的线路列表
     */
    @NonNull
    public List<RouteDirection> getAllRouteDirections()
    {
        return routeDirectionRepository.findAll();
    }

    /**
     * 根据线路ID获取该线路下所有带方向的线路
     */
    @NonNull
    public List<RouteDirection> getRouteDirectionsByRouteId(Long routeId)
    {
        return routeDirectionRepository.findByRouteId(routeId);
    }
}
