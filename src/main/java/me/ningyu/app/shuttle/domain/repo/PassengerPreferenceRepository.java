package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.PassengerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 乘客偏好 Repository
 */
@Repository
public interface PassengerPreferenceRepository extends JpaRepository<PassengerPreference, Long>, PagingAndSortingRepository<PassengerPreference, Long>, JpaSpecificationExecutor<PassengerPreference>
{
    /**
     * 查询某乘客的所有偏好设置
     */
    List<PassengerPreference> findByPassengerId(Long passengerId);

    /**
     * 查询某乘客的默认主要偏好
     */
    @Query("SELECT pp FROM passenger_preference pp WHERE pp.passenger.id = :passengerId AND pp.isPrimary = true")
    Optional<PassengerPreference> findPrimaryPreferenceByPassengerId(@Param("passengerId") Long passengerId);

    /**
     * 查询某线路方向的常乘客列表（用于统计）
     */
    @Query("SELECT pp FROM passenger_preference pp WHERE pp.routeDirection.id = :routeDirectionId")
    List<PassengerPreference> findByRouteDirectionId(@Param("routeDirectionId") Long routeDirectionId);

    /**
     * 查询某站点的常乘客列表
     */
    @Query("SELECT pp FROM passenger_preference pp WHERE pp.stop.id = :stopId")
    List<PassengerPreference> findByStopId(@Param("stopId") Long stopId);
}
