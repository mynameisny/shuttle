package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteDirectionRepository extends JpaRepository<RouteDirection, Long>, PagingAndSortingRepository<RouteDirection, Long>, JpaSpecificationExecutor<RouteDirection>
{
    boolean existsByRouteId(Long routeId);

    boolean existsByCode(String code);

    List<RouteDirection> findByRouteId(Long routeId);
}
