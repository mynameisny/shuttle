package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Route;
import me.ningyu.app.shuttle.domain.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long>, PagingAndSortingRepository<RouteStop, Long>, JpaSpecificationExecutor<RouteStop>
{
    @Query("SELECT rs FROM route_stop rs WHERE rs.route = :route ORDER BY rs.sequence ASC")
    List<RouteStop> findByRouteOrderBySequenceAsc(@Param("route") Route route);

    @Query("SELECT rs FROM route_stop rs WHERE rs.route = :route ORDER BY rs.sequence ASC")
    Optional<RouteStop> findFirstByRouteOrderBySequenceAsc(@Param("route") Route route);

    @Query("SELECT rs FROM route_stop rs WHERE rs.route.id = :routeId ORDER BY rs.sequence ASC")
    Optional<RouteStop> findFirstByRouteIdOrderBySequenceAsc(@Param("routeId") Long routeId);

    boolean existsByRouteId(Long routeId);
}
