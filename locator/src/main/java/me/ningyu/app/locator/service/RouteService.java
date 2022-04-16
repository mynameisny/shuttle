package me.ningyu.app.locator.service;

import me.ningyu.app.locator.common.vo.RouteDto;
import me.ningyu.app.locator.domain.route.entity.Route;
import me.ningyu.app.locator.domain.route.repository.RouteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class RouteService
{
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository)
    {
        this.routeRepository = routeRepository;
    }

    public Route add(RouteDto dto)
    {
        Route entity = new Route();
        BeanUtils.copyProperties(dto, entity);
        return routeRepository.save(entity);
    }
}
