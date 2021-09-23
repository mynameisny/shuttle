package me.ningyu.app.shuttleserver.repository;

import me.ningyu.app.shuttleserver.entity.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>
{
}
