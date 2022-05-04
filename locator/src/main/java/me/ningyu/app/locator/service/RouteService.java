package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.RouteDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.route.entity.Route;
import me.ningyu.app.locator.domain.route.repository.RouteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class RouteService
{
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository)
    {
        this.routeRepository = routeRepository;
    }

    @Transactional
    public Route add(RouteDto dto)
    {
        Route entity = new Route();
        BeanUtils.copyProperties(dto, entity);
        return routeRepository.save(entity);
    }

    @Transactional
    public void remove(String id)
    {
        Optional.of(routeRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("路线%s不存在", id)));
        routeRepository.deleteById(id);
    }

    @Transactional
    public Route update(String id, RouteDto dto)
    {
        Route route = Optional.of(routeRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("路线%s不存在", id)));
        BeanUtils.copyProperties(route, dto);
        return routeRepository.save(route);
    }

    public RouteDto get(String id)
    {
        Route route  = Optional.of(routeRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("路线%s不存在", id)));
        RouteDto dto = new RouteDto();
        BeanUtils.copyProperties(route, dto);
        return dto;
    }

    public Page<Route> list(Predicate predicate, Pageable pageable)
    {
        return routeRepository.findAll(predicate, pageable);
    }
}
