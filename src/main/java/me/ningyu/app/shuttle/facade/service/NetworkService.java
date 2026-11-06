package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Network;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.RouteNetwork;
import me.ningyu.app.shuttle.domain.repo.NetworkRepository;
import me.ningyu.app.shuttle.domain.repo.RouteNetworkRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.model.network.CreateNetworkRequest;
import me.ningyu.app.shuttle.model.network.RouteNetworkTagsView;
import me.ningyu.app.shuttle.model.network.UpdateNetworkRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 线路网络（GTFS networks.txt + route_networks.txt）。
 * <p>
 * routes.network_id 为单值，只能表达"一个"维度（如去哪个办公区）；
 * 需要多标签（目的地 + 出发方位 + 跨城等）时改用 route_networks.txt，
 * 一条线路可挂多个网络。规范中两种方式互斥，启用本表后 routes.network_id 必须留空。
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NetworkService
{
    private final NetworkRepository networkRepository;
    private final RouteNetworkRepository routeNetworkRepository;
    private final RouteRepository routeRepository;

    @Nonnull
    public Network createNetwork(CreateNetworkRequest request)
    {
        if (networkRepository.existsByNetworkId(request.getNetworkId()))
        {
            throw new IllegalArgumentException("网络ID已存在");
        }
        Network network = Network.builder()
                .networkId(request.getNetworkId())
                .networkName(request.getNetworkName())
                .build();
        return networkRepository.save(network);
    }

    @Nonnull
    public Network updateNetwork(Long id, UpdateNetworkRequest request)
    {
        Network network = networkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("网络不存在"));
        if (request.getNetworkName() != null)
        {
            network.setNetworkName(request.getNetworkName());
        }
        return networkRepository.save(network);
    }

    public void deleteNetwork(Long id)
    {
        Network network = networkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("网络不存在"));
        if (!routeNetworkRepository.findByNetworkNetworkId(network.getNetworkId()).isEmpty())
        {
            throw new IllegalStateException("该网络下仍有线路，不能删除");
        }
        networkRepository.delete(network);
    }

    @Nonnull
    @Transactional(readOnly = true)
    public List<Network> getAllNetworks()
    {
        return networkRepository.findAllByOrderByNetworkIdAsc();
    }

    @Nonnull
    @Transactional(readOnly = true)
    public Network getNetworkById(Long id)
    {
        return networkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("网络不存在"));
    }

    /**
     * 全量覆盖某线路的网络标签：先删后建，传空集合即清空。
     */
    @Nonnull
    public RouteNetworkTagsView setRouteNetworks(Long routePk, Collection<String> networkIds)
    {
        Route route = routeRepository.findById(routePk)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        List<String> wanted = networkIds == null ? List.of()
                : networkIds.stream().filter(s -> s != null && !s.isBlank()).distinct().toList();

        // 校验网络存在
        List<Network> networks = new ArrayList<>();
        for (String nid : wanted)
        {
            networks.add(networkRepository.findByNetworkId(nid)
                    .orElseThrow(() -> new IllegalArgumentException("网络不存在: " + nid)));
        }

        routeNetworkRepository.deleteAll(routeNetworkRepository.findByRouteId(routePk));
        List<RouteNetwork> saved = new ArrayList<>();
        for (Network network : networks)
        {
            saved.add(routeNetworkRepository.save(RouteNetwork.builder().route(route).network(network).build()));
        }
        log.info("线路 {} 网络标签更新为 {}", route.getRouteId(), wanted);

        return RouteNetworkTagsView.builder()
                .routePk(route.getId())
                .routeGtfsId(route.getRouteId())
                .routeShortName(route.getRouteShortName())
                .routeLongName(route.getRouteLongName())
                .tags(saved.stream()
                        .map(rn -> RouteNetworkTagsView.Tag.builder()
                                .networkId(rn.getNetwork().getNetworkId())
                                .networkName(rn.getNetwork().getNetworkName())
                                .build())
                        .toList())
                .build();
    }

    /**
     * 新增单个标签（幂等：已存在则直接返回）。
     */
    public void addRouteNetwork(Long routePk, String networkId)
    {
        Route route = routeRepository.findById(routePk)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));
        if (routeNetworkRepository.existsByRouteIdAndNetworkNetworkId(routePk, networkId))
        {
            return;
        }
        Network network = networkRepository.findByNetworkId(networkId)
                .orElseThrow(() -> new IllegalArgumentException("网络不存在: " + networkId));
        routeNetworkRepository.save(RouteNetwork.builder().route(route).network(network).build());
    }

    /**
     * 移除单个标签。
     */
    public void removeRouteNetwork(Long routePk, String networkId)
    {
        routeNetworkRepository.deleteByRouteIdAndNetworkNetworkId(routePk, networkId);
    }

    @Nonnull
    @Transactional(readOnly = true)
    public RouteNetworkTagsView getRouteNetworks(Long routePk)
    {
        Route route = routeRepository.findById(routePk)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));
        List<RouteNetwork> links = routeNetworkRepository.findByRouteId(routePk);
        return RouteNetworkTagsView.builder()
                .routePk(route.getId())
                .routeGtfsId(route.getRouteId())
                .routeShortName(route.getRouteShortName())
                .routeLongName(route.getRouteLongName())
                .tags(links.stream()
                        .map(rn -> RouteNetworkTagsView.Tag.builder()
                                .networkId(rn.getNetwork().getNetworkId())
                                .networkName(rn.getNetwork().getNetworkName())
                                .build())
                        .toList())
                .build();
    }

    /**
     * 批量取全部线路的标签，按线路主键分组（列表渲染用，避免 N+1）。
     */
    @Nonnull
    @Transactional(readOnly = true)
    public Map<Long, List<RouteNetworkTagsView.Tag>> getTagsGroupedByRoute(List<Long> routePks)
    {
        Map<Long, List<RouteNetworkTagsView.Tag>> result = new LinkedHashMap<>();
        if (routePks == null || routePks.isEmpty())
        {
            return result;
        }
        for (Object[] row : routeNetworkRepository.findTagsByRouteIds(routePks))
        {
            Long routePk = (Long) row[0];
            result.computeIfAbsent(routePk, k -> new ArrayList<>())
                    .add(RouteNetworkTagsView.Tag.builder()
                            .networkId((String) row[1])
                            .networkName((String) row[2])
                            .build());
        }
        return result;
    }
}
