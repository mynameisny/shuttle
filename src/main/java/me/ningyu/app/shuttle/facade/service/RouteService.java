package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Agency;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.domain.repo.AgencyRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.StopTimeRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.model.route.CreateRouteRequest;
import me.ningyu.app.shuttle.model.route.RouteStopsView;
import me.ningyu.app.shuttle.model.route.UpdateRouteRequest;
import me.ningyu.app.shuttle.model.network.RouteNetworkTagsView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    private final NetworkService networkService;

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
        // 按 route_sort_order 升序，保证业务含义相近的线路分组相邻
        return routeRepository.findAllByOrderByRouteSortOrderAsc();
    }

    /**
     * 线路列表 + 网络标签（多标签，对应 GTFS route_networks.txt）。
     * 一次批量查出全部标签，避免前端逐条请求造成 N+1。
     */
    @Nonnull
    public List<RouteNetworkTagsView> getAllRoutesWithTags(Long agencyId)
    {
        List<Route> routes = agencyId != null
                ? routeRepository.findByAgencyIdOrderByRouteSortOrderAsc(agencyId)
                : routeRepository.findAllByOrderByRouteSortOrderAsc();

        Map<Long, List<RouteNetworkTagsView.Tag>> tagMap =
                networkService.getTagsGroupedByRoute(routes.stream().map(Route::getId).toList());

        return routes.stream()
                .map(r -> RouteNetworkTagsView.builder()
                        .routePk(r.getId())
                        .routeGtfsId(r.getRouteId())
                        .routeShortName(r.getRouteShortName())
                        .routeLongName(r.getRouteLongName())
                        .routeDesc(r.getRouteDesc())
                        .routeSortOrder(r.getRouteSortOrder())
                        .tags(tagMap.getOrDefault(r.getId(), List.of()))
                        .build())
                .toList();
    }

    @Nonnull
    public List<Route> getRoutesByAgencyId(Long agencyId)
    {
        return routeRepository.findByAgencyIdOrderByRouteSortOrderAsc(agencyId);
    }

    /**
     * 线路站点聚合（正向查询）：线路 → 全部班次停站 → 站点，按方向分组。
     * 方向内同站去重（并集），按方向内最小站序排列；同名去程/返程站台靠方向上下文消歧。
     * GTFS 实体属性不变，仅只读聚合展示。
     */
    @Nonnull
    public RouteStopsView getRouteStops(Long routePk)
    {
        Route route = routeRepository.findById(routePk)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在: id=" + routePk));

        List<StopTime> stopTimes = stopTimeRepository.findByRouteIdFetchStopAndTrip(routePk);

        // 方向 -> stopPk -> 该方向的停站记录（同站多班次聚合）
        Map<Integer, Map<Long, List<StopTime>>> byDirection = new TreeMap<>();
        for (StopTime st : stopTimes)
        {
            if (st.getStop() == null)
            {
                continue; // location_group / location 型停站无实体站点
            }
            int dir = st.getTrip() != null && st.getTrip().getDirectionId() != null ? st.getTrip().getDirectionId() : 0;
            byDirection.computeIfAbsent(dir, k -> new LinkedHashMap<>())
                    .computeIfAbsent(st.getStop().getId(), k -> new ArrayList<>())
                    .add(st);
        }

        List<RouteStopsView.DirectionStops> directions = byDirection.entrySet().stream()
                .map(directionEntry -> {
                    Map<Long, List<StopTime>> stopMap = directionEntry.getValue();
                    // 班次按首遇顺序收集（LinkedHashMap 保序），label 取 trip_short_name 回退 trip_id
                    Map<Long, Trip> tripById = new LinkedHashMap<>();
                    stopMap.values().stream()
                            .flatMap(List::stream)
                            .forEach(st -> tripById.putIfAbsent(st.getTrip().getId(), st.getTrip()));
                    List<Long> tripPks = List.copyOf(tripById.keySet());
                    List<String> tripLabels = tripById.values().stream()
                            .map(t -> t.getTripShortName() != null && !t.getTripShortName().isBlank()
                                    ? t.getTripShortName() : t.getTripId())
                            .collect(Collectors.toList());
                    List<RouteStopsView.StopItem> stops = stopMap.values().stream()
                            .map(records -> {
                                StopTime first = records.get(0);
                                Stop stop = first.getStop();
                                return RouteStopsView.StopItem.builder()
                                        .stopPk(stop.getId())
                                        .stopId(stop.getStopId())
                                        .stopName(stop.getStopName())
                                        .lat(stop.getStopLat())
                                        .lng(stop.getStopLon())
                                        .stopSequence(records.stream().mapToInt(StopTime::getStopSequence).min().orElse(0))
                                        .tripCount((int) records.stream().map(st2 -> st2.getTrip().getId()).distinct().count())
                                        .build();
                            })
                            .sorted(Comparator.comparing(RouteStopsView.StopItem::getStopSequence))
                            .collect(Collectors.toList());
                    return RouteStopsView.DirectionStops.builder()
                            .directionId(directionEntry.getKey())
                            .tripCount(tripPks.size())
                            .tripPks(tripPks)
                            .tripLabels(tripLabels)
                            .stops(stops)
                            .build();
                })
                .collect(Collectors.toList());

        return RouteStopsView.builder()
                .routePk(route.getId())
                .routeGtfsId(route.getRouteId())
                .routeShortName(route.getRouteShortName())
                .routeLongName(route.getRouteLongName())
                .directions(directions)
                .build();
    }
}