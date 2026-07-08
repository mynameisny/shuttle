package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Line;
import me.ningyu.app.shuttle.domain.entity.Route;
import me.ningyu.app.shuttle.domain.repo.LineRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.RouteStopRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.model.route.CreateRouteRequest;
import me.ningyu.app.shuttle.model.route.UpdateRouteRequest;
import org.springframework.lang.NonNull;
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
    private final LineRepository lineRepository;
    private final RouteStopRepository routeStopRepository;
    private final TripRepository tripRepository;

    @NonNull
    public Route createRoute(CreateRouteRequest request)
    {
        Line line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        Route route = Route.builder()
                .line(line)
                .name(request.getName())
                .direction(request.getDirection())
                .shape(request.getShape())
                .build();

        return routeRepository.save(route);
    }

    @NonNull
    public Route updateRoute(Long id, UpdateRouteRequest request)
    {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));

        Line line = lineRepository.findById(request.getLineId())
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        route.setLine(line);
        route.setName(request.getName());
        route.setDirection(request.getDirection());
        route.setShape(request.getShape());

        return routeRepository.save(route);
    }

    public void deleteRoute(Long id)
    {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));

        if (routeStopRepository.existsByRouteId(id))
        {
            throw new IllegalStateException("该线路方向下存在站点，不能删除");
        }

        if (tripRepository.existsByRouteId(id))
        {
            throw new IllegalStateException("该线路方向存在班次记录，不能删除");
        }

        routeRepository.delete(route);
    }

    @NonNull
    public Route getRouteById(Long id)
    {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));
    }

    @NonNull
    public List<Route> getAllRoutes()
    {
        return routeRepository.findAll();
    }

    @NonNull
    public List<Route> getRoutesByLineId(Long lineId)
    {
        return routeRepository.findByLineId(lineId);
    }
}
