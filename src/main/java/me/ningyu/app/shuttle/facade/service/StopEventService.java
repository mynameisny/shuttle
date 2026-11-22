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
import me.ningyu.app.shuttle.model.fleet.TripAnalysisView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** 准时判定阈值：实际与计划偏差绝对值 ≤ 该秒数（180 = ±3 分钟）视为准时。 */
    private static final int ON_TIME_THRESHOLD_SEC = 180;

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
     * 运营分析（日期范围版）：某班次在一段日期内、每个「运营日」各站点的计划/实际到发明细 + 逐日准时率。
     * <p>合并 stop_times（全部站点与计划时刻，每天相同）与 stop_event（实际时刻，按日期 + stop_sequence 对齐）；
     * 日期范围内的「非运营日」（周末/停运日）被剔除，只返回运营日；未上报实际时刻的站点实际值留空、不计入准时率分母。
     */
    @Transactional(readOnly = true)
    public TripAnalysisView analyze(String tripId, LocalDate startDate, LocalDate endDate)
    {
        if (tripId == null || tripId.isBlank())
            throw new IllegalArgumentException("请指定班次");
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("请指定日期范围");
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("开始日期不能晚于结束日期");

        Trip trip = tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + tripId));
        Route route = trip.getRoute();

        List<StopTime> stopTimes = stopTimeRepository.findByTripOrderByStopSequenceAsc(trip);

        // 日期范围内实际运营日（剔除周末/停运日）
        List<LocalDate> activeDates = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1))
        {
            if (calendarService.isServiceActive(trip.getServiceId(), d))
                activeDates.add(d);
        }

        // 一次取区间内全部事件，按 (日期, 站序) 索引
        List<StopEvent> events = stopEventRepository
                .findByTripTripIdAndEventDateBetweenOrderByEventDateAscStopSequenceAsc(tripId, startDate, endDate);
        Map<LocalDate, Map<Integer, StopEvent>> byDateSeq = new HashMap<>();
        for (StopEvent e : events)
        {
            byDateSeq.computeIfAbsent(e.getEventDate(), k -> new HashMap<>())
                    .putIfAbsent(e.getStopSequence(), e);
        }

        // 站点 × 运营日 明细矩阵
        List<TripAnalysisView.StopItem> stops = new ArrayList<>();
        for (StopTime st : stopTimes)
        {
            List<TripAnalysisView.DayData> days = new ArrayList<>();
            for (LocalDate date : activeDates)
            {
                Map<Integer, StopEvent> evMap = byDateSeq.get(date);
                StopEvent ev = evMap != null ? evMap.get(st.getStopSequence()) : null;
                LocalTime actualArr = ev != null ? ev.getActualArrivalTime() : null;
                LocalTime actualDep = ev != null ? ev.getActualDepartureTime() : null;
                days.add(TripAnalysisView.DayData.builder()
                        .eventDate(date)
                        .actualArrival(actualArr)
                        .actualDeparture(actualDep)
                        .arrivalDelaySec(delaySec(actualArr, st.getArrivalTime()))
                        .departureDelaySec(delaySec(actualDep, st.getDepartureTime()))
                        .build());
            }
            Stop s = st.getStop();
            stops.add(TripAnalysisView.StopItem.builder()
                    .stopSequence(st.getStopSequence())
                    .stopName(s != null ? s.getStopName() : null)
                    .scheduledArrival(st.getArrivalTime())
                    .scheduledDeparture(st.getDepartureTime())
                    .days(days)
                    .build());
        }

        // 逐日准时率（柱状图数据源）
        List<TripAnalysisView.DailyPunctuality> daily = new ArrayList<>();
        for (LocalDate date : activeDates)
        {
            int arrRep = 0, arrOnTime = 0, arrEarly = 0, arrLate = 0;
            int depRep = 0, depOnTime = 0, depEarly = 0, depLate = 0;
            Map<Integer, StopEvent> evMap = byDateSeq.get(date);
            for (StopTime st : stopTimes)
            {
                StopEvent ev = evMap != null ? evMap.get(st.getStopSequence()) : null;
                Long arrDelay = delaySec(ev != null ? ev.getActualArrivalTime() : null, st.getArrivalTime());
                Long depDelay = delaySec(ev != null ? ev.getActualDepartureTime() : null, st.getDepartureTime());
                if (arrDelay != null)
                {
                    arrRep++;
                    if (Math.abs(arrDelay) <= ON_TIME_THRESHOLD_SEC) arrOnTime++;
                    else if (arrDelay < 0) arrEarly++;
                    else arrLate++;
                }
                if (depDelay != null)
                {
                    depRep++;
                    if (Math.abs(depDelay) <= ON_TIME_THRESHOLD_SEC) depOnTime++;
                    else if (depDelay < 0) depEarly++;
                    else depLate++;
                }
            }
            double arrP = arrRep > 0 ? arrOnTime * 100.0 / arrRep : 0;
            double depP = depRep > 0 ? depOnTime * 100.0 / depRep : 0;
            int reported = arrRep + depRep;
            int onTime = arrOnTime + depOnTime;
            double overall = reported > 0 ? onTime * 100.0 / reported : 0;
            daily.add(TripAnalysisView.DailyPunctuality.builder()
                    .eventDate(date)
                    .arrivalReported(arrRep).departureReported(depRep)
                    .arrivalOnTime(arrOnTime).departureOnTime(depOnTime)
                    .arrivalEarly(arrEarly).arrivalLate(arrLate)
                    .departureEarly(depEarly).departureLate(depLate)
                    .arrivalPunctuality(arrP).departurePunctuality(depP)
                    .overallPunctuality(overall)
                    .build());
        }

        TripAnalysisView.Summary summary = TripAnalysisView.Summary.builder()
                .totalStops(stopTimes.size())
                .onTimeThresholdSec(ON_TIME_THRESHOLD_SEC)
                .daily(daily)
                .build();

        return TripAnalysisView.builder()
                .tripId(trip.getTripId())
                .tripLabel(trip.getTripShortName())
                .routeId(route != null ? route.getRouteId() : null)
                .routeLabel(route != null ? route.getRouteLongName() : null)
                .directionId(trip.getDirectionId())
                .startDate(startDate)
                .endDate(endDate)
                .activeDates(activeDates)
                .stops(stops)
                .summary(summary)
                .build();
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
