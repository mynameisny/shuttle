package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Route;
import me.ningyu.app.shuttle.domain.repo.RouteDirectionRepository;
import me.ningyu.app.shuttle.domain.repo.RouteDirectionStopRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.model.route.CreateRouteRequest;
import me.ningyu.app.shuttle.model.route.UpdateRouteRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RouteService
{
    private final RouteRepository routeRepository;
    private final RouteDirectionRepository routeDirectionRepository;
    private final RouteDirectionStopRepository routeDirectionStopRepository;


    /**
     * 创建线路
     */
    @NonNull
    public Route createRoute(CreateRouteRequest request)
    {
        Route route = Route.builder()
                .name(request.getName())
                .direction(request.getDirection())
                .description(request.getDescription())
                .sequence(request.getSequence() != null ? request.getSequence() : 0)
                .manager(request.getManager())
                .build();

        return routeRepository.save(route);
    }

    /**
     * 更新线路
     */
    @NonNull
    public Route updateRoute(Long id, UpdateRouteRequest request)
    {
        Route route = routeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        route.setName(request.getName());
        route.setDirection(request.getDirection());
        route.setDescription(request.getDescription());
        route.setSequence(request.getSequence() != null ? request.getSequence() : 0);
        route.setManager(request.getManager());

        return routeRepository.save(route);
    }

    /**
     * 删除线路
     * 如果线路下有带方向的线路，不允许删除
     */
    public void deleteRoute(Long id)
    {
        Route route = routeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        // 检查是否有关联的带方向线路
        if (routeDirectionRepository.existsByRouteId(id))
        {
            throw new IllegalStateException("该线路下存在带方向的线路，不能删除");
        }

        routeRepository.delete(route);
    }

    /**
     * 获取线路详情
     */
    @NonNull
    public Route getRouteById(Long id)
    {
        return routeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("线路不存在"));
    }

    /**
     * 获取所有线路列表
     */
    @NonNull
    public List<Route> getAllRoutes()
    {
        return routeRepository.findAll();
    }
}
