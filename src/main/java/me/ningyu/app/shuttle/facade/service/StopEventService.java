package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.entity.extension.StopEvent;
import me.ningyu.app.shuttle.domain.entity.extension.TripAssignment;
import me.ningyu.app.shuttle.domain.entity.extension.TripSubstitution;
import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.domain.repo.StopEventRepository;
import me.ningyu.app.shuttle.domain.repo.StopTimeRepository;
import me.ningyu.app.shuttle.domain.repo.TripAssignmentRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.domain.repo.TripSubstitutionRepository;
import me.ningyu.app.shuttle.domain.repo.VehicleRepository;
import me.ningyu.app.shuttle.enums.ReportSource;
import me.ningyu.app.shuttle.enums.StopEventType;
import me.ningyu.app.shuttle.model.fleet.ReportStopEventRequest;
import me.ningyu.app.shuttle.model.fleet.StopEventView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 站点到发记录服务：手动 / 设备上报的实际运行数据。
 *
 * <p>上报时按「班次 + 日期 + 站序」upsert 一条记录，到站 / 发车分别填充实际时刻；
 * 计划时刻取自 stop_times，当班车辆 / 司机 / 车长取自线路分配（代班优先）。
 */
@Service
@RequiredArgsConstructor
public class StopEventService
{
    private final StopEventRepository stopEventRepository;
    private final TripRepository tripRepository;
    private final StopTimeRepository stopTimeRepository;
    private final TripAssignmentRepository tripAssignmentRepository;
    private final TripSubstitutionRepository tripSubstitutionRepository;
    private final VehicleRepository vehicleRepository;
    private final CalendarService calendarService;

    @Transactional(readOnly = true)
    public List<StopEventView> list(String tripId, LocalDate eventDate)
    {
        List<StopEvent> events;
        if (tripId != null && !tripId.isBlank() && eventDate != null)
            events = stopEventRepository.findByTripTripIdAndEventDateOrderByStopSequenceAsc(tripId, eventDate);
        else if (tripId != null && !tripId.isBlank())
            events = stopEventRepository.findByTripTripIdOrderByEventDateDescStopSequenceAsc(tripId);
        else if (eventDate != null)
            events = stopEventRepository.findByEventDateOrderByStopSequenceAsc(eventDate);
        else
            events = stopEventRepository.findAllByOrderByEventDateDescStopSequenceAsc();
        return events.stream().map(this::toView).toList();
    }

    /**
     * 上报一次到站 / 发车（手动与设备共用）。upsert 语义：同班次同日期同站序只保留一条记录。
     */
    @Transactional
    public StopEventView report(ReportStopEventRequest req)
    {
        Trip trip = tripRepository.findByTripId(req.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + req.getTripId()));

        // 校验当天该班次是否运营（calendar + calendar_dates 例外）
        if (!calendarService.isServiceActive(trip.getServiceId(), req.getEventDate()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "该班次在 " + req.getEventDate() + " 不运营（服务日历停运或非运营日）");
        }

        // 定位站点与站序（stopId 或 stopSequence 至少给一个），并顺带取计划时刻
        List<StopTime> stopTimes = stopTimeRepository.findByTripOrderByStopSequenceAsc(trip);
        StopTime target = null;
        if (req.getStopSequence() != null)
        {
            target = stopTimes.stream()
                    .filter(st -> req.getStopSequence().equals(st.getStopSequence()))
                    .findFirst().orElse(null);
        }
        else if (req.getStopId() != null && !req.getStopId().isBlank())
        {
            target = stopTimes.stream()
                    .filter(st -> st.getStop() != null && req.getStopId().equals(st.getStop().getStopId()))
                    .findFirst().orElse(null);
        }
        if (target == null)
            throw new IllegalArgumentException("站点不在该班次的停站序列中（请核对 stopId / stopSequence）");
        Stop stop = target.getStop();
        Integer stopSequence = target.getStopSequence();

        // 当班人员：线路级分配（车辆/司机/车长），代班优先，设备可按车牌覆盖车辆
        Vehicle vehicle = null;
        Driver driver = null;
        Captain captain = null;
        Route route = trip.getRoute();
        TripAssignment assignment = route != null
                ? tripAssignmentRepository.findByRouteRouteId(route.getRouteId()).orElse(null) : null;
        if (assignment != null)
        {
            vehicle = assignment.getVehicle();
            driver = assignment.getDriver();
            captain = assignment.getCaptain();
        }
        List<TripSubstitution> subs = tripSubstitutionRepository
                .findByTripTripIdAndSubstituteDate(req.getTripId(), req.getEventDate());
        if (!subs.isEmpty())
            driver = subs.get(0).getSubstituteDriver();
        if (req.getVehiclePlate() != null && !req.getVehiclePlate().isBlank())
            vehicle = vehicleRepository.findByPlateNumber(req.getVehiclePlate()).orElse(vehicle);

        // upsert：班次 + 日期 + 站序唯一，存在则补全，不存在则新建
        StopEvent ev = stopEventRepository
                .findByTripTripIdAndEventDateAndStopSequence(req.getTripId(), req.getEventDate(), stopSequence)
                .orElse(null);
        if (ev == null)
        {
            ev = StopEvent.builder()
                    .trip(trip)
                    .stop(stop)
                    .stopSequence(stopSequence)
                    .eventDate(req.getEventDate())
                    .scheduledArrivalTime(target.getArrivalTime())
                    .scheduledDepartureTime(target.getDepartureTime())
                    .vehicle(vehicle)
                    .driver(driver)
                    .captain(captain)
                    .source(req.getSource() != null ? req.getSource() : ReportSource.MANUAL)
                    .build();
        }
        else
        {
            // 已有记录补齐此前缺失的车辆/人员
            if (ev.getVehicle() == null) ev.setVehicle(vehicle);
            if (ev.getDriver() == null) ev.setDriver(driver);
            if (ev.getCaptain() == null) ev.setCaptain(captain);
            if (req.getSource() != null) ev.setSource(req.getSource());
        }

        if (req.getEventType() == StopEventType.ARRIVAL)
            ev.setActualArrivalTime(req.getActualTime());
        else
            ev.setActualDepartureTime(req.getActualTime());

        if (req.getReportedBy() != null && !req.getReportedBy().isBlank())
            ev.setReportedBy(req.getReportedBy());
        if (req.getRemark() != null && !req.getRemark().isBlank())
            ev.setRemark(req.getRemark());

        return toView(stopEventRepository.save(ev));
    }

    @Transactional
    public void delete(Long id)
    {
        stopEventRepository.deleteById(id);
    }

    private StopEventView toView(StopEvent e)
    {
        Trip t = e.getTrip();
        Route r = t != null ? t.getRoute() : null;
        Stop s = e.getStop();
        Vehicle v = e.getVehicle();
        Driver d = e.getDriver();
        Captain c = e.getCaptain();
        return StopEventView.builder()
                .id(e.getId())
                .tripId(t != null ? t.getTripId() : null)
                .tripLabel(t != null ? t.getTripShortName() : null)
                .routeId(r != null ? r.getRouteId() : null)
                .routeLabel(r != null ? r.getRouteLongName() : null)
                .directionId(t != null ? t.getDirectionId() : null)
                .stopId(s != null ? s.getStopId() : null)
                .stopName(s != null ? s.getStopName() : null)
                .stopSequence(e.getStopSequence())
                .eventDate(e.getEventDate())
                .scheduledArrival(e.getScheduledArrivalTime())
                .scheduledDeparture(e.getScheduledDepartureTime())
                .actualArrival(e.getActualArrivalTime())
                .actualDeparture(e.getActualDepartureTime())
                .vehiclePlate(v != null ? v.getPlateNumber() : null)
                .driverName(d != null ? d.getName() : null)
                .captainName(c != null ? c.getName() : null)
                .source(e.getSource())
                .sourceName(e.getSource() != null ? e.getSource().getName() : null)
                .reportedBy(e.getReportedBy())
                .remark(e.getRemark())
                .arrivalDelaySec(delaySec(e.getActualArrivalTime(), e.getScheduledArrivalTime()))
                .departureDelaySec(delaySec(e.getActualDepartureTime(), e.getScheduledDepartureTime()))
                .build();
    }

    private Long delaySec(LocalTime actual, LocalTime scheduled)
    {
        if (actual == null || scheduled == null) return null;
        return (long) (actual.toSecondOfDay() - scheduled.toSecondOfDay());
    }
}
