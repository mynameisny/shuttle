package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long>, PagingAndSortingRepository<Trip, Long>, JpaSpecificationExecutor<Trip>
{
    Optional<Trip> findByTripId(String tripId);

    boolean existsByTripId(String tripId);

    List<Trip> findByRouteId(Long routeId);

    boolean existsByRouteId(Long routeId);

    List<Trip> findByServiceId(String serviceId);
}