package me.ningyu.app.shuttle.facade.service;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.enums.BikeAllowance;
import me.ningyu.app.shuttle.enums.WheelchairAccessibility;
import me.ningyu.app.shuttle.model.schedule.CreateTripRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateTripRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TripService
{
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

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