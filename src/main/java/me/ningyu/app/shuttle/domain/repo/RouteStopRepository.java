package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import me.ningyu.app.shuttle.domain.entity.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 线路停靠站点 Repository
 * 按照 GUIDES.md 规范，原 RouteDirectionStop 改为 RouteStop
 */
@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long>, PagingAndSortingRepository<RouteStop, Long>, JpaSpecificationExecutor<RouteStop>
{
    /**
     * 根据线路方向，按顺序升序获取第一个停靠点（即首站）
     * 用于确定班次的发车时间
     *
     * @param routeDirection 线路方向实体
     * @return 首站停靠信息（Optional）
     */
    @Query("SELECT rs FROM route_stop rs " +
            "WHERE rs.directionalRoute = :routeDirection " +
            "ORDER BY rs.sequence ASC")
    Optional<RouteStop> findFirstByRouteDirectionOrderBySequenceAsc(@Param("routeDirection") RouteDirection routeDirection);

    /**
     * （可选）根据线路方向ID获取首站
     */
    @Query("SELECT rs FROM route_stop rs " +
            "WHERE rs.directionalRoute.id = :routeDirectionId " +
            "ORDER BY rs.sequence ASC")
    Optional<RouteStop> findFirstByRouteDirectionIdOrderBySequenceAsc(@Param("routeDirectionId") Long routeDirectionId);

    /**
     * 检查线路方向下是否存在站点
     */
    boolean existsByDirectionalRouteId(Long routeDirectionId);
}
