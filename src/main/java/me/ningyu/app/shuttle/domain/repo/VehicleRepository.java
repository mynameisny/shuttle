package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>
{
    boolean existsByPlateNumber(String plateNumber);

    boolean existsByDriverId(Long driverId);
}
