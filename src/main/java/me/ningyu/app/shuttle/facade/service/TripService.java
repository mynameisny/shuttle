package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.StopRepository;
import me.ningyu.app.shuttle.domain.repo.StopTimeRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.enums.BikeAllowance;
import me.ningyu.app.shuttle.enums.Timepoint;
import me.ningyu.app.shuttle.enums.WheelchairAccessibility;
import me.ningyu.app.shuttle.model.schedule.AddStopTimeRequest;
import me.ningyu.app.shuttle.model.schedule.CreateTripRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateStopTimeRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateTripRequest;
import me.ningyu.app.shuttle.model.stop.StopUsageView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TripService
{
    /** GTFS 时刻文本：HH:MM 或 HH:MM:SS（小时 1-2 位） */
    private static final Pattern GTFS_TIME_PATTERN = Pattern.compile("^(\\d{1,2}):(\\d{2})(?::(\\d{2}))?$");

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final StopTimeRepository stopTimeRepository;
    private final StopRepository stopRepository;

    /**
     * 追加停站到班次末尾（管理端站点维护页"线路下加站"）。
     * stop_sequence 按现有最大站序 +1 分配（GTFS 允许非连续递增；空班次从 1 起）；
     * 同班次重复停靠同一站点拒绝（班车场景重复必为误操作）。
     * 到/发时间必填，复用 GTFS 时刻解析与到发先后校验。
     */
    @Nonnull
    public StopUsageView addStopTime(Long tripPk, AddStopTimeRequest request)
    {
        Trip trip = tripRepository.findById(tripPk)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "班次不存在: id=" + tripPk));
        Stop stop = stopRepository.findById(request.getStopPk())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "站点不存在: id=" + request.getStopPk()));
        List<StopTime> existing = stopTimeRepository.findByTripOrderByStopSequenceAsc(trip);
        boolean duplicated = existing.stream()
                .anyMatch(st -> st.getStop() != null && st.getStop().getId().equals(request.getStopPk()));
        if (duplicated)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "班次 " + trip.getTripId() + " 已包含站点「" + stop.getStopName() + "」");
        }
        int nextSeq = existing.stream()
                .mapToInt(StopTime::getStopSequence)
                .max()
                .orElse(0) + 1;

        LocalTime arrival = parseGtfsTime(request.getArrivalTime(), "arrivalTime");
        LocalTime departure = parseGtfsTime(request.getDepartureTime(), "departureTime");
        if (arrival.isAfter(departure))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "到站时间不能晚于发车时间");
        }

        StopTime saved = stopTimeRepository.save(StopTime.builder()
                .trip(trip)
                .stop(stop)
                .stopSequence(nextSeq)
                .arrivalTime(arrival)
                .departureTime(departure)
                .build());
        return StopUsageView.of(saved);
    }

    @Nonnull
    public Trip createTrip(CreateTripRequest request)
    {
        if (tripRepository.existsByTripId(request.getTripId()))
        {
            throw new IllegalArgumentException("班次ID已存在: " + request.getTripId());
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("线路不存在"));

        Trip trip = Trip.builder()
                .tripId(request.getTripId())
                .route(route)
                .serviceId(request.getServiceId())
                .directionId(request.getDirectionId() != null ? request.getDirectionId() : 0)
                .tripHeadsign(request.getTripHeadsign())
                .tripShortName(request.getTripShortName())
                .blockId(request.getBlockId())
                .shapeId(request.getShapeId())
                .build();

        if (request.getWheelchairAccessible() != null)
        {
            trip.setWheelchairAccessible(WheelchairAccessibility.fromValue(request.getWheelchairAccessible()));
        }
        if (request.getBikesAllowed() != null)
        {
            trip.setBikesAllowed(BikeAllowance.fromValue(request.getBikesAllowed()));
        }

        return tripRepository.save(trip);
    }

    @Nonnull
    public Trip updateTrip(Long id, UpdateTripRequest request)
    {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        if (request.getTripId() != null && !request.getTripId().isBlank()
                && !request.getTripId().equals(trip.getTripId()))
        {
            if (tripRepository.existsByTripId(request.getTripId()))
            {
                throw new IllegalArgumentException("班次ID已存在: " + request.getTripId());
            }
            trip.setTripId(request.getTripId());
        }
        if (request.getServiceId() != null) trip.setServiceId(request.getServiceId());
        if (request.getDirectionId() != null) trip.setDirectionId(request.getDirectionId());
        if (request.getTripHeadsign() != null) trip.setTripHeadsign(request.getTripHeadsign());
        if (request.getTripShortName() != null) trip.setTripShortName(request.getTripShortName());
        if (request.getBlockId() != null) trip.setBlockId(request.getBlockId());
        if (request.getShapeId() != null) trip.setShapeId(request.getShapeId());
        if (request.getWheelchairAccessible() != null)
        {
            trip.setWheelchairAccessible(WheelchairAccessibility.fromValue(request.getWheelchairAccessible()));
        }
        if (request.getBikesAllowed() != null)
        {
            trip.setBikesAllowed(BikeAllowance.fromValue(request.getBikesAllowed()));
        }

        return tripRepository.save(trip);
    }

    public void cancelTrip(Long id)
    {
        if (!tripRepository.existsById(id))
        {
            throw new IllegalArgumentException("班次不存在");
        }
        tripRepository.deleteById(id);
    }

    @Nonnull
    public Trip getTripById(Long id)
    {
        return tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在"));
    }

    @Nonnull
    public Trip getTripByTripId(String tripId)
    {
        return tripRepository.findByTripId(tripId)
                .orElseThrow(() -> new IllegalArgumentException("班次不存在: " + tripId));
    }

    /**
     * 按 GTFS trip_id 查找班次（可选语义，供集合筛选使用：无匹配返回空而非报错）。
     */
    @Nonnull
    public Optional<Trip> findTripByGtfsTripId(String tripId)
    {
        return tripRepository.findByTripId(tripId);
    }

    /**
     * 更新班次中指定站序的停站时刻（管理端站点维护页内联编辑）。
     * 定位采用 (tripPk, stopSequence)，与 GTFS stop_times 主键语义一致。
     * 字段语义：null=不修改；空白串=清空；否则解析设置（支持 HH:MM / HH:MM:SS，
     * 不支持 GTFS 跨日 24:00+，班车场景足够）。到站不得晚于发车（按合并后值校验）。
     */
    /**
     * 重排班次停站顺序（管理端站点维护页分组视图拖拽）。
     * 按传入的有序站点主键列表重写 stop_sequence（1 起连续递增，满足 GTFS 非负递增要求）；
     * 到/发时刻等字段随行整体移动，不做时间轴重排。
     * 含 location_group/location 型停站的班次不支持重排（无法用站点主键定位）。
     */
    public void reorderStopTimes(Long tripPk, List<Long> orderedStopPks)
    {
        Trip trip = tripRepository.findById(tripPk)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "班次不存在: id=" + tripPk));
        if (orderedStopPks == null || orderedStopPks.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "停站顺序列表不能为空");
        }
        Set<Long> distinctPks = new LinkedHashSet<>(orderedStopPks);
        if (distinctPks.size() != orderedStopPks.size())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "停站顺序列表含重复站点");
        }
        List<StopTime> stopTimes = stopTimeRepository.findByTripOrderByStopSequenceAsc(trip);
        Map<Long, StopTime> byStopPk = new LinkedHashMap<>();
        for (StopTime st : stopTimes)
        {
            if (st.getStop() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "班次 " + trip.getTripId() + " 含非实体站点停站，不支持拖拽重排");
            }
            byStopPk.put(st.getStop().getId(), st);
        }
        if (!byStopPk.keySet().equals(distinctPks))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "停站列表与班次现有停站不一致，请刷新后重试");
        }
        for (int i = 0; i < orderedStopPks.size(); i++)
        {
            byStopPk.get(orderedStopPks.get(i)).setStopSequence(i + 1);
        }
    }

    @Nonnull
    public StopUsageView updateStopTime(Long tripPk, Integer stopSequence, UpdateStopTimeRequest request)
    {
        Trip trip = tripRepository.findById(tripPk)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "班次不存在: id=" + tripPk));
        StopTime stopTime = stopTimeRepository.findByTripAndStopSequence(trip, stopSequence)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "班次 " + trip.getTripId() + " 不存在站序 " + stopSequence));

        LocalTime newArrival = request.getArrivalTime() != null
                ? parseGtfsTime(request.getArrivalTime(), "arrivalTime") : stopTime.getArrivalTime();
        LocalTime newDeparture = request.getDepartureTime() != null
                ? parseGtfsTime(request.getDepartureTime(), "departureTime") : stopTime.getDepartureTime();
        if (newArrival != null && newDeparture != null && newArrival.isAfter(newDeparture))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "到站时间不能晚于发车时间");
        }
        if (request.getArrivalTime() != null) stopTime.setArrivalTime(newArrival);
        if (request.getDepartureTime() != null) stopTime.setDepartureTime(newDeparture);
        if (request.getTimepoint() != null)
        {
            if (request.getTimepoint().isBlank())
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "timepoint 不能为空白（可选 APPROXIMATE/EXACT）");
            }
            try
            {
                stopTime.setTimepoint(Timepoint.valueOf(request.getTimepoint().trim().toUpperCase(Locale.ROOT)));
            }
            catch (IllegalArgumentException e)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "timepoint 非法: " + request.getTimepoint() + "（可选 APPROXIMATE/EXACT）");
            }
        }

        StopTime saved = stopTimeRepository.save(stopTime);
        return StopUsageView.of(saved);
    }

    /**
     * 解析 GTFS 时刻文本为 LocalTime：空白返回 null（清空语义）；
     * 格式非法或超出 00:00:00-23:59:59 范围时抛 400（带可读原因）。
     */
    private LocalTime parseGtfsTime(String text, String field)
    {
        if (text.isBlank())
        {
            return null;
        }
        Matcher m = GTFS_TIME_PATTERN.matcher(text.trim());
        if (!m.matches())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    field + " 时间格式非法: \"" + text + "\"（支持 HH:MM 或 HH:MM:SS）");
        }
        try
        {
            return LocalTime.of(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                    m.group(3) != null ? Integer.parseInt(m.group(3)) : 0);
        }
        catch (DateTimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " 时间超出范围: " + text);
        }
    }

    @Nonnull
    public List<Trip> getTripsByRouteId(Long routeId)
    {
        return tripRepository.findByRouteId(routeId);
    }

    @Nonnull
    public List<Trip> getTripsByServiceId(String serviceId)
    {
        return tripRepository.findByServiceId(serviceId);
    }

    @Nonnull
    public List<Trip> getAllTrips()
    {
        return tripRepository.findAll();
    }
}