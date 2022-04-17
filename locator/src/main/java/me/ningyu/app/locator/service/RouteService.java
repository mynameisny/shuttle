package me.ningyu.app.locator.service;

import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.RouteDto;
import me.ningyu.app.locator.domain.route.entity.Route;
import me.ningyu.app.locator.domain.route.repository.RouteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public void remove(String id)
    {
        Optional.of(routeRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("路线%s不存在", id)));
        routeRepository.deleteById(id);
    }
}
