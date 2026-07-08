package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long>, PagingAndSortingRepository<Route, Long>, JpaSpecificationExecutor<Route>
{
    List<Route> findByLineId(Long lineId);

    boolean existsByLineId(Long lineId);
}
