package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.RouteNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteNetworkRepository extends JpaRepository<RouteNetwork, Long>, PagingAndSortingRepository<RouteNetwork, Long>, JpaSpecificationExecutor<RouteNetwork>
{
    /**
     * 一条线路可挂多个网络（如同时属于"昌平园区""北向"），用于多标签筛选。
     */
    List<RouteNetwork> findByRouteId(Long routePk);

    List<RouteNetwork> findByNetworkNetworkId(String networkId);

    boolean existsByRouteIdAndNetworkNetworkId(Long routePk, String networkId);

    long deleteByRouteIdAndNetworkNetworkId(Long routePk, String networkId);

    /**
     * 批量取线路 → 网络标签，避免前端渲染列表时 N+1。
     * 返回 [routePk(Long), networkId(String), networkName(String)]。
     */
    @Query("select rn.route.id, rn.network.networkId, rn.network.networkName from route_network rn where rn.route.id in :routeIds")
    List<Object[]> findTagsByRouteIds(@Param("routeIds") List<Long> routeIds);
}
