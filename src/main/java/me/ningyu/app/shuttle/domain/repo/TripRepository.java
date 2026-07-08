package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.Trip;
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
public interface TripRepository extends JpaRepository<Trip, Long>, PagingAndSortingRepository<Trip, Long>, JpaSpecificationExecutor<Trip>
{
    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.driver_id = :driverId " +
            "AND s.service_date = :date " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByDriverIdAndServiceDateAndTimeOverlap(
            @Param("driverId") Long driverId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime);

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.vehicle_id = :vehicleId " +
            "AND s.service_date = :date " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByVehicleIdAndServiceDateAndTimeOverlap(
            @Param("vehicleId") Long vehicleId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime);

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.driver_id = :driverId " +
            "AND s.service_date = :date " +
            "AND s.id != :excludeId " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByDriverIdAndServiceDateAndTimeOverlapExcludingId(
            @Param("driverId") Long driverId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime,
            @Param("excludeId") Long excludeId);

    @Query(value = "SELECT COUNT(*) > 0 FROM shuttle_schedule s " +
            "WHERE s.vehicle_id = :vehicleId " +
            "AND s.service_date = :date " +
            "AND s.id != :excludeId " +
            "AND (" +
            "  :newTime BETWEEN s.departure_time AND DATE_ADD(s.departure_time, INTERVAL 3 HOUR) " +
            "  OR s.departure_time BETWEEN :newTime AND DATE_ADD(:newTime, INTERVAL 3 HOUR)" +
            ")", nativeQuery = true)
    boolean existsByVehicleIdAndServiceDateAndTimeOverlapExcludingId(
            @Param("vehicleId") Long vehicleId,
            @Param("date") LocalDate date,
            @Param("newTime") LocalTime newTime,
            @Param("excludeId") Long excludeId);

    List<Trip> findByServiceDate(LocalDate date);

    List<Trip> findByDriverIdAndServiceDateGreaterThanEqualOrderByServiceDateAsc(
            Long driverId, LocalDate date);

    boolean existsByRouteId(Long routeId);
}
