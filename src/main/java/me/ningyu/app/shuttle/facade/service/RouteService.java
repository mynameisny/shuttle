package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Agency;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.repo.AgencyRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.StopTimeRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.model.route.CreateRouteRequest;
import me.ningyu.app.shuttle.model.route.UpdateRouteRequest;
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
    private final AgencyRepository agencyRepository;
    private final StopTimeRepository stopTimeRepository;
    private final TripRepository tripRepository;

    @Nonnull
    public Route createRoute(CreateRouteRequest request)
    {
        Agency agency = agencyRepository.findById(request.getAgencyId())
                .orElseThrow(() -> new IllegalArgumentException("机构不存在"));

        Route route = Route.builder()
                .routeId(request.getRouteId())
                .agency(agency)
                .routeLongName(request.getRouteLongName())
                .routeShortName(request.getRouteShortName())
                .routeDesc(request.getRouteDesc())
                .routeType(request.getRouteType())
                .routeUrl(request.getRouteUrl())
                .routeColor(request.getRouteColor())
                .routeTextColor(request.getRouteTextColor())
                .routeSortOrder(request.getRouteSortOrder())
                .continuousPickup(request.getContinuousPickup())
                .continuousDropOff(request.getContinuousDropOff())
                .networkId(request.getNetworkId())
                .build();

        return routeRepository.save(route);
    }

    @Nonnull
    public Route updateRoute(Long id, UpdateRouteRequest request)
    {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        if (request.getRouteLongName() != null) route.setRouteLongName(request.getRouteLongName());
        if (request.getRouteShortName() != null) route.setRouteShortName(request.getRouteShortName());
        if (request.getRouteDesc() != null) route.setRouteDesc(request.getRouteDesc());
        if (request.getRouteType() != null) route.setRouteType(request.getRouteType());
        if (request.getRouteUrl() != null) route.setRouteUrl(request.getRouteUrl());
        if (request.getRouteColor() != null) route.setRouteColor(request.getRouteColor());
        if (request.getRouteTextColor() != null) route.setRouteTextColor(request.getRouteTextColor());
        if (request.getRouteSortOrder() != null) route.setRouteSortOrder(request.getRouteSortOrder());
        if (request.getContinuousPickup() != null) route.setContinuousPickup(request.getContinuousPickup());
        if (request.getContinuousDropOff() != null) route.setContinuousDropOff(request.getContinuousDropOff());
        if (request.getNetworkId() != null) route.setNetworkId(request.getNetworkId());

        return routeRepository.save(route);
    }

    public void deleteRoute(Long id)
    {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        if (stopTimeRepository.existsByTripRouteId(id))
        {
            throw new IllegalStateException("该线路下存在站点，不能删除");
        }
        if (tripRepository.existsByRouteId(id))
        {
            throw new IllegalStateException("该线路下存在班次，不能删除");
        }

        routeRepository.delete(route);
    }

    @Nonnull
    public Route getRouteById(Long id)
    {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));
    }

    @Nonnull
    public List<Route> getAllRoutes()
    {
        return routeRepository.findAll();
    }

    @Nonnull
    public List<Route> getRoutesByAgencyId(Long agencyId)
    {
        return routeRepository.findByAgencyId(agencyId);
    }
}