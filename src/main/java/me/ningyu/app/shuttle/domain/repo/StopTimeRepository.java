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

    @Query("SELECT st FROM stop_time st WHERE st.trip = :trip ORDER BY st.stopSequence ASC")
    Optional<StopTime> findFirstByTripOrderByStopSequenceAsc(@Param("trip") Trip trip);

    @Query("SELECT st FROM stop_time st WHERE st.trip.id = :tripId ORDER BY st.stopSequence ASC")
    Optional<StopTime> findFirstByTripIdOrderByStopSequenceAsc(@Param("tripId") Long tripId);

    @Query("SELECT CASE WHEN COUNT(st) > 0 THEN true ELSE false END FROM stop_time st WHERE st.trip.route.id = :routeId")
    boolean existsByTripRouteId(@Param("routeId") Long routeId);

    @Query("SELECT st FROM stop_time st WHERE st.stop.id = :stopId ORDER BY st.stopSequence ASC")
    List<StopTime> findByStopId(@Param("stopId") Long stopId);
}
