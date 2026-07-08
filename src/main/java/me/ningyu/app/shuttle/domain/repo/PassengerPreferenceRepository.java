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

@Repository
public interface PassengerPreferenceRepository extends JpaRepository<PassengerPreference, Long>, PagingAndSortingRepository<PassengerPreference, Long>, JpaSpecificationExecutor<PassengerPreference>
{
    List<PassengerPreference> findByPassengerId(Long passengerId);

    @Query("SELECT pp FROM passenger_preference pp WHERE pp.passenger.id = :passengerId AND pp.isPrimary = true")
    Optional<PassengerPreference> findPrimaryPreferenceByPassengerId(@Param("passengerId") Long passengerId);

    @Query("SELECT pp FROM passenger_preference pp WHERE pp.route.id = :routeId")
    List<PassengerPreference> findByRouteId(@Param("routeId") Long routeId);

    @Query("SELECT pp FROM passenger_preference pp WHERE pp.stop.id = :stopId")
    List<PassengerPreference> findByStopId(@Param("stopId") Long stopId);
}
