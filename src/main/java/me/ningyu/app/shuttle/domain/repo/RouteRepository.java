package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Route;
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
}