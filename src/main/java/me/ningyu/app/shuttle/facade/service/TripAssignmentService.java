package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.entity.extension.TripAssignment;
import me.ningyu.app.shuttle.domain.entity.extension.TripSubstitution;
import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.domain.repo.CaptainRepository;
import me.ningyu.app.shuttle.domain.repo.DriverRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.TripAssignmentRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.domain.repo.TripSubstitutionRepository;
import me.ningyu.app.shuttle.domain.repo.VehicleRepository;
import me.ningyu.app.shuttle.model.fleet.CreateAssignmentRequest;
import me.ningyu.app.shuttle.model.fleet.SubstituteDriverRequest;
import me.ningyu.app.shuttle.model.fleet.TripAssignmentView;
import me.ningyu.app.shuttle.model.fleet.UpdateAssignmentRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripAssignmentService
{
    private final TripAssignmentRepository tripAssignmentRepository;
    private final TripSubstitutionRepository tripSubstitutionRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final CaptainRepository captainRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

    @Transactional(readOnly = true)
    public List<TripAssignmentView> list()
    {
        return tripAssignmentRepository.findAll().stream()
                .map(this::toView)
                .toList();
    }

    @Transactional
    public TripAssignmentView create(CreateAssignmentRequest req)
    {
        if (req.getRouteId() == null || req.getRouteId().isBlank())
            throw new IllegalArgumentException("线路不能为空");
        Route route = routeRepository.findByRouteId(req.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("线路不存在: " + req.getRouteId()));
        tripAssignmentRepository.findByRouteRouteId(req.getRouteId())
                .ifPresent(a -> { throw new IllegalStateException("该线路已存在分配，请使用编辑"); });

        Vehicle v = req.getVehicleId() != null
                ? vehicleRepository.findById(req.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在")) : null;
        Driver d = req.getDriverId() != null
                ? driverRepository.findById(req.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在")) : null;
        Captain c = req.getCaptainId() != null
                ? captainRepository.findById(req.getCaptainId()).orElseThrow(() -> new IllegalArgumentException("车长不存在")) : null;

        TripAssignment a = tripAssignmentRepository.save(TripAssignment.builder()
                .route(route)
                .vehicle(v)
                .driver(d)
                .captain(c)
                .build());
        return toView(a);
    }

    @Transactional
    public TripAssignmentView update(Long id, UpdateAssignmentRequest req)
    {
        TripAssignment a = tripAssignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("线路分配不存在: id=" + id));
        if (req.getVehicleId() != null)
            a.setVehicle(vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("车辆不存在")));
        if (req.getDriverId() != null)
            a.setDriver(driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new IllegalArgumentException("司机不存在")));
        if (req.getCaptainId() != null)
            a.setCaptain(captainRepository.findById(req.getCaptainId())
                    .orElseThrow(() -> new IllegalArgumentException("车长不存在")));
        return toView(tripAssignmentRepository.save(a));
    }

    @Transactional(readOnly = true)
    public List<TripSubstitution> listSubstitutions(String tripId)
    {
        return tripSubstitutionRepository.findByTripTripId(tripId);
    }

    @Transactional
    public TripSubstitution addSubstitution(String tripId, SubstituteDriverRequest req)
    {
        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + tripId));
        Driver sub = driverRepository.findById(req.getSubstituteDriverId())
                .orElseThrow(() -> new IllegalArgumentException("代班司机不存在"));
        return tripSubstitutionRepository.save(TripSubstitution.builder()
                .trip(trip)
                .substituteDate(req.getSubstituteDate())
                .substituteDriver(sub)
                .reason(req.getReason())
                .build());
    }

    @Transactional
    public void deleteSubstitution(Long id)
    {
        tripSubstitutionRepository.deleteById(id);
    }

    private TripAssignmentView toView(TripAssignment a)
    {
        Route r = a.getRoute();
        Vehicle v = a.getVehicle();
        Driver d = a.getDriver();
        Captain c = a.getCaptain();
        String outbound = null, inbound = null;
        if (r != null)
        {
            for (Trip t : tripRepository.findByRouteId(r.getId()))
            {
                if (t.getDirectionId() != null && t.getDirectionId() == 1)
                    inbound = t.getTripId();
                else
                    outbound = t.getTripId();
            }
        }
        return TripAssignmentView.builder()
                .id(a.getId())
                .routeId(r != null ? r.getRouteId() : null)
                .routeLabel(r != null ? r.getRouteLongName() : null)
                .routeColor(r != null ? r.getRouteColor() : null)
                .outboundTripId(outbound)
                .inboundTripId(inbound)
                .vehicleId(v != null ? v.getId() : null)
                .vehiclePlate(v != null ? v.getPlateNumber() : null)
                .driverId(d != null ? d.getId() : null)
                .driverName(d != null ? d.getName() : null)
                .captainId(c != null ? c.getId() : null)
                .captainName(c != null ? c.getName() : null)
                .build();
    }
}
