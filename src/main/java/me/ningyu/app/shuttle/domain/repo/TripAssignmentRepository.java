package me.ningyu.app.shuttle.domain.repo;

import me.ningyu.app.shuttle.domain.entity.extension.TripAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripAssignmentRepository extends JpaRepository<TripAssignment, Long>
{
    /**
     * 按线路查询分配（一条线路一条长期绑定）。
     */
    Optional<TripAssignment> findByRouteRouteId(String routeId);

    /**
     * 按车辆查询分配（一辆车可绑定多个线路，或复用）。
     */
    List<TripAssignment> findByVehicleId(Long vehicleId);
}
