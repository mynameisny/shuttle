package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import me.ningyu.app.shuttle.domain.entity.RouteDirectionStop;
import me.ningyu.app.shuttle.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteDirectionStopRepository extends JpaRepository<RouteDirectionStop, Long>, PagingAndSortingRepository<RouteDirectionStop, Long>, JpaSpecificationExecutor<RouteDirectionStop>
{
    /**
     * 根据线路方向，按顺序升序获取第一个停靠点（即首站）
     * 用于确定班次的发车时间
     *
     * @param routeDirection 线路方向实体
     * @return 首站停靠信息（Optional）
     */
    @Query("SELECT rds FROM route_direction_stop rds " +
            "WHERE rds.directionalRoute = :routeDirection " +
            "ORDER BY rds.sequence ASC")
    Optional<RouteDirectionStop> findFirstByRouteDirectionOrderBySequenceAsc(@Param("routeDirection") RouteDirection routeDirection);

    /**
     * （可选）根据线路方向ID获取首站
     */
    @Query("SELECT rds FROM route_direction_stop rds " +
            "WHERE rds.directionalRoute.id = :routeDirectionId " +
            "ORDER BY rds.sequence ASC")
    Optional<RouteDirectionStop> findFirstByRouteDirectionIdOrderBySequenceAsc(@Param("routeDirectionId") Long routeDirectionId);
}
