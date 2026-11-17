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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * 按线路主键查当班信息（车辆/司机/车长），供乘客端时刻表等展示。
     */
    @Transactional(readOnly = true)
    public TripAssignmentView getByRoutePk(Long routePk)
    {
        Route route = routeRepository.findById(routePk)
                .orElseThrow(() -> new IllegalArgumentException("线路不存在: id=" + routePk));
        TripAssignment a = tripAssignmentRepository.findByRouteRouteId(route.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("该线路未配置车辆/司机/车长: " + route.getRouteId()));
        return toView(a);
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

        validateDriverAndCaptain(d, c, null);

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
        Vehicle v = req.getVehicleId() != null
                ? vehicleRepository.findById(req.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在")) : a.getVehicle();
        Driver d = req.getDriverId() != null
                ? driverRepository.findById(req.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在")) : a.getDriver();
        Captain c = req.getCaptainId() != null
                ? captainRepository.findById(req.getCaptainId()).orElseThrow(() -> new IllegalArgumentException("车长不存在")) : a.getCaptain();

        validateDriverAndCaptain(d, c, id);

        a.setVehicle(v);
        a.setDriver(d);
        a.setCaptain(c);
        return toView(tripAssignmentRepository.save(a));
    }

    /**
     * 校验司机 / 车长是否已被其他线路分配（一人一线，禁止重复）。
     *
     * @param excludeId 排除当前分配的 id（新增时传 null）
     */
    private void validateDriverAndCaptain(Driver d, Captain c, Long excludeId)
    {
        if (d != null)
        {
            tripAssignmentRepository.findByDriverId(d.getId()).stream()
                    .filter(a -> excludeId == null || !a.getId().equals(excludeId))
                    .findFirst()
                    .ifPresent(a -> { throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "司机 " + d.getName() + " 已被线路「" + routeLabel(a.getRoute()) + "」分配"); });
        }
        if (c != null)
        {
            tripAssignmentRepository.findByCaptainId(c.getId()).stream()
                    .filter(a -> excludeId == null || !a.getId().equals(excludeId))
                    .findFirst()
                    .ifPresent(a -> { throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "车长 " + c.getName() + " 已被线路「" + routeLabel(a.getRoute()) + "」分配"); });
        }
    }

    private String routeLabel(Route r)
    {
        if (r == null) return "未知";
        if (r.getRouteLongName() != null && !r.getRouteLongName().isBlank()) return r.getRouteLongName();
        if (r.getRouteShortName() != null && !r.getRouteShortName().isBlank()) return r.getRouteShortName();
        return r.getRouteId();
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

    /**
     * 删除一条线路分配（解除线路 → 车辆/司机/车长 的绑定）。
     */
    @Transactional
    public void delete(Long id)
    {
        tripAssignmentRepository.deleteById(id);
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
                .vehicleType(v != null ? v.getVehicleType() : null)
                .seatCount(v != null ? v.getSeatCount() : null)
                .vehiclePhotos(v != null ? v.getPhotos() : null)
                .driverId(d != null ? d.getId() : null)
                .driverName(d != null ? d.getName() : null)
                .driverPhone(d != null ? d.getPhone() : null)
                .driverAvatar(d != null ? d.getAvatarUrl() : null)
                .captainId(c != null ? c.getId() : null)
                .captainName(c != null ? c.getName() : null)
                .captainPhone(c != null ? c.getPhone() : null)
                .captainAvatar(c != null ? c.getAvatarUrl() : null)
                .build();
    }
}
