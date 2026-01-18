package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.ShuttleSchedule;
import me.ningyu.app.shuttle.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShuttleScheduleRepository extends JpaRepository<ShuttleSchedule, Long>, PagingAndSortingRepository<ShuttleSchedule, Long>, JpaSpecificationExecutor<ShuttleSchedule>
{

    // ========================
    // 冲突检测：创建新班次时使用（不包含自身）
    // 使用 nativeQuery = true 支持 MySQL INTERVAL
    // ========================

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.driver_id = :driverId " +
            "AND s.operating_date = :date " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByDriverIdAndOperatingDateAndTimeOverlap(
            @Param("driverId") Long driverId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime);

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.vehicle_id = :vehicleId " +
            "AND s.operating_date = :date " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByVehicleIdAndOperatingDateAndTimeOverlap(
            @Param("vehicleId") Long vehicleId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime);

    // ========================
    // 编辑时排除当前班次
    // ========================

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.driver_id = :driverId " +
            "AND s.operating_date = :date " +
            "AND s.id != :excludeId " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByDriverIdAndOperatingDateAndTimeOverlapExcludingId(
            @Param("driverId") Long driverId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime,
            @Param("excludeId") Long excludeId);

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.vehicle_id = :vehicleId " +
            "AND s.operating_date = :date " +
            "AND s.id != :excludeId " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByVehicleIdAndOperatingDateAndTimeOverlapExcludingId(
            @Param("vehicleId") Long vehicleId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime,
            @Param("excludeId") Long excludeId);

    // ========================
    // 其他 JPQL 查询（非时间冲突）
    // ========================

    List<ShuttleSchedule> findByOperatingDate(LocalDate date);

    List<ShuttleSchedule> findByDriverIdAndOperatingDateGreaterThanEqualOrderByOperatingDateAsc(
            Long driverId, LocalDate date);

    /**
     * 检查线路方向下是否存在排班
     */
    boolean existsByRouteDirectionId(Long routeDirectionId);
}