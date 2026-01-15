package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteDirectionRepository extends JpaRepository<RouteDirection, Long>, PagingAndSortingRepository<RouteDirection, Long>, JpaSpecificationExecutor<RouteDirection>
{
}
