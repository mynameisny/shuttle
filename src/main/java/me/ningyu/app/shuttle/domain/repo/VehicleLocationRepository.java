package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long>, PagingAndSortingRepository<VehicleLocation, Long>, JpaSpecificationExecutor<VehicleLocation>
{
    Optional<VehicleLocation> findTopByVehicleIdOrderByGpsTimeDesc(Long vehicleId);

    List<VehicleLocation> findByTripIdOrderByGpsTimeAsc(Long tripId);
}
