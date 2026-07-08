package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.*;
import me.ningyu.app.shuttle.domain.repo.*;
import me.ningyu.app.shuttle.model.schedule.CreateScheduleRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateScheduleRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TripService
{
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteStopRepository routeStopRepository;
    private final RideRepository rideRepository;

    public List<Trip> createBulkTrips(CreateScheduleRequest request)
    {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException("司机不存在"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("车辆不存在"));

        LocalTime departureTime = getFirstStopDepartureTime(route);

        List<LocalDate> dates = generateDates(request.getStartDate(), request.getEndDate(), request.getIncludeWeekends());
        List<Trip> trips = new ArrayList<>();

        for (LocalDate date : dates)
        {
            validateNoConflict(driver.getId(), vehicle.getId(), date, departureTime);

            Trip trip = Trip.builder()
                    .route(route)
                    .driver(driver)
                    .vehicle(vehicle)
                    .serviceDate(date)
                    .departureTime(departureTime)
                    .build();
            trips.add(trip);
        }

        return tripRepository.saveAll(trips);
    }

    public Trip updateTrip(Long tripId, UpdateScheduleRequest request)
    {
        if (!request.getServiceDate().isAfter(LocalDate.now()) && !request.getServiceDate().isEqual(LocalDate.now()))
        {
            throw new IllegalArgumentException("只能编辑今天及未来的班次");
        }

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new IllegalArgumentException("班次不存在"));
        Driver driver = driverRepository.findById(request.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在"));

        validateNoConflictExceptSelf(driver.getId(), vehicle.getId(), request.getServiceDate(), request.getDepartureTime(), tripId);

        trip.setDriver(driver);
        trip.setVehicle(vehicle);
        trip.setServiceDate(request.getServiceDate());
        trip.setDepartureTime(request.getDepartureTime());

        return tripRepository.save(trip);
    }

    public void cancelTrip(Long tripId)
    {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        if (trip.getServiceDate().isBefore(LocalDate.now()))
        {
            throw new IllegalArgumentException("不能取消历史班次");
        }

        if (rideRepository.existsByTripId(tripId))
        {
            throw new IllegalStateException("该班次已有乘客记录，不能取消");
        }

        tripRepository.deleteById(tripId);
    }

    @NonNull
    public List<Trip> getTripsByDate(LocalDate date)
    {
        return tripRepository.findByServiceDate(date);
    }

    @NonNull
    public Trip getTripById(Long id)
    {
        return tripRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("班次不存在"));
    }

    // ========== 内部工具方法 ==========

    private List<LocalDate> generateDates(LocalDate start, LocalDate end, Boolean includeWeekends)
    {
        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1))
        {
            if (includeWeekends || !isWeekend(date))
            {
                dates.add(date);
            }
        }
        return dates;
    }

    private boolean isWeekend(LocalDate date)
    {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private LocalTime getFirstStopDepartureTime(Route route)
    {
        return routeStopRepository.findFirstByRouteOrderBySequenceAsc(route)
                .map(rs -> LocalTime.of(0, 0))
                .orElseThrow(() -> new IllegalStateException("线路方向未配置站点"));
    }

    private void validateNoConflict(Long driverId, Long vehicleId, LocalDate date, LocalTime departureTime)
    {
        if (tripRepository.existsByDriverIdAndServiceDateAndTimeOverlap(driverId, date, departureTime))
        {
            throw new IllegalArgumentException("该司机在指定时间已有其他班次");
        }
        if (tripRepository.existsByVehicleIdAndServiceDateAndTimeOverlap(vehicleId, date, departureTime))
        {
            throw new IllegalArgumentException("该车辆在指定时间已被其他班次占用");
        }
    }

    private void validateNoConflictExceptSelf(Long driverId, Long vehicleId, LocalDate date, LocalTime departureTime, Long excludeTripId)
    {
        if (tripRepository.existsByDriverIdAndServiceDateAndTimeOverlapExcludingId(driverId, date, departureTime, excludeTripId))
        {
            throw new IllegalArgumentException("该司机在指定时间已有其他班次");
        }
        if (tripRepository.existsByVehicleIdAndServiceDateAndTimeOverlapExcludingId(vehicleId, date, departureTime, excludeTripId))
        {
            throw new IllegalArgumentException("该车辆在指定时间已被其他班次占用");
        }
    }
}
