package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, Long>, PagingAndSortingRepository<StopTime, Long>, JpaSpecificationExecutor<StopTime>
{
    @Query("SELECT st FROM stop_time st WHERE st.trip = :trip ORDER BY st.stopSequence ASC")
    List<StopTime> findByTripOrderByStopSequenceAsc(@Param("trip") Trip trip);

    @Query("SELECT st FROM stop_time st WHERE st.trip = :trip AND st.stopSequence = :stopSequence")
    Optional<StopTime> findByTripAndStopSequence(@Param("trip") Trip trip, @Param("stopSequence") Integer stopSequence);

    @Query("SELECT st FROM stop_time st WHERE st.trip = :trip ORDER BY st.stopSequence ASC")
    Optional<StopTime> findFirstByTripOrderByStopSequenceAsc(@Param("trip") Trip trip);

    @Query("SELECT st FROM stop_time st WHERE st.trip.id = :tripId ORDER BY st.stopSequence ASC")
    Optional<StopTime> findFirstByTripIdOrderByStopSequenceAsc(@Param("tripId") Long tripId);

    @Query("SELECT CASE WHEN COUNT(st) > 0 THEN true ELSE false END FROM stop_time st WHERE st.trip.route.id = :routeId")
    boolean existsByTripRouteId(@Param("routeId") Long routeId);

    @Query("SELECT st FROM stop_time st WHERE st.stop.id = :stopId ORDER BY st.stopSequence ASC")
    List<StopTime> findByStopId(@Param("stopId") Long stopId);

    @Query("SELECT st FROM stop_time st JOIN FETCH st.stop JOIN FETCH st.trip WHERE st.trip.route.id = :routeId ORDER BY st.stopSequence ASC")
    List<StopTime> findByRouteIdFetchStopAndTrip(@Param("routeId") Long routeId);

    /**
     * 全表停站引用投影（stopPk/routePk/线路简称/方向/班次），用于站点引用摘要批量消歧。
     * 内连接：无线路或无站点的停站记录自然排除。
     * 按「线路+方向+班次」去重（见 StopService.getUsageSummary）：一条线路多班次时，
     * 只按线路+方向去重会把三个班次折叠成一个角标，看不出站点到底被哪个班次引用。
     */
    @Query("SELECT st.stop.id AS stopPk, r.id AS routePk, r.routeShortName AS routeShortName, " +
            "t.directionId AS directionId, t.id AS tripPk, t.tripShortName AS tripShortName, t.tripId AS tripId " +
            "FROM stop_time st JOIN st.trip t JOIN t.route r " +
            "ORDER BY r.routeShortName ASC, t.directionId ASC, t.id ASC, st.stopSequence ASC")
    List<StopRefProjection> findAllStopRefs();

    interface StopRefProjection
    {
        Long getStopPk();

        Long getRoutePk();

        String getRouteShortName();

        Integer getDirectionId();

        Long getTripPk();

        String getTripShortName();

        String getTripId();
    }
}
