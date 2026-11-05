package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long>, PagingAndSortingRepository<Route, Long>, JpaSpecificationExecutor<Route>
{
    Optional<Route> findByRouteId(String routeId);

    boolean existsByRouteId(String routeId);

    List<Route> findByAgencyId(Long agencyId);

    /**
     * 按展示排序返回全部线路（route_sort_order 升序，空值排最后），
     * 使业务含义相近的线路（同一 network_id 分组）在列表中相邻。
     */
    List<Route> findAllByOrderByRouteSortOrderAsc();

    /**
     * 按展示排序返回指定机构下的线路（排序规则同上）。
     */
    List<Route> findByAgencyIdOrderByRouteSortOrderAsc(Long agencyId);
}