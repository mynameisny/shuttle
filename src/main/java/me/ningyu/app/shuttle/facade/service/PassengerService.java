package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Passenger;
import me.ningyu.app.shuttle.domain.entity.PassengerPreference;
import me.ningyu.app.shuttle.domain.entity.Route;
import me.ningyu.app.shuttle.domain.entity.Stop;
import me.ningyu.app.shuttle.domain.repo.PassengerPreferenceRepository;
import me.ningyu.app.shuttle.domain.repo.PassengerRepository;
import me.ningyu.app.shuttle.domain.repo.RouteRepository;
import me.ningyu.app.shuttle.domain.repo.StopRepository;
import me.ningyu.app.shuttle.model.passenger.CreatePassengerPreferenceRequest;
import me.ningyu.app.shuttle.model.passenger.CreatePassengerRequest;
import me.ningyu.app.shuttle.model.passenger.UpdatePassengerRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PassengerService
{
    private final PassengerRepository passengerRepository;
    private final PassengerPreferenceRepository preferenceRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;

    @NonNull
    public Passenger createPassenger(CreatePassengerRequest request)
    {
        if (passengerRepository.existsByEmployeeNumber(request.getEmployeeNumber()))
        {
            throw new IllegalArgumentException("员工编号已存在: " + request.getEmployeeNumber());
        }

        Passenger passenger = Passenger.builder()
                .name(request.getName())
                .employeeNumber(request.getEmployeeNumber())
                .phone(request.getPhone())
                .type(request.getType())
                .build();

        return passengerRepository.save(passenger);
    }

    @NonNull
    public Passenger updatePassenger(Long id, UpdatePassengerRequest request)
    {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));

        passenger.setName(request.getName());
        passenger.setPhone(request.getPhone());
        passenger.setType(request.getType());

        return passengerRepository.save(passenger);
    }

    public void deletePassenger(Long id)
    {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));

        passengerRepository.delete(passenger);
    }

    @NonNull
    public Passenger getPassengerById(Long id)
    {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));
    }

    @NonNull
    public Optional<Passenger> getPassengerByEmployeeNumber(String employeeNumber)
    {
        return passengerRepository.findByEmployeeNumber(employeeNumber);
    }

    @NonNull
    public List<Passenger> getAllPassengers()
    {
        return passengerRepository.findAll();
    }

    @NonNull
    public PassengerPreference addPassengerPreference(CreatePassengerPreferenceRequest request)
    {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));

        Stop stop = stopRepository.findById(request.getStopId())
                .orElseThrow(() -> new IllegalArgumentException("停靠点不存在"));

        if (request.getIsPrimary() != null && request.getIsPrimary())
        {
            Optional<PassengerPreference> existingPrimary =
                    preferenceRepository.findPrimaryPreferenceByPassengerId(request.getPassengerId());
            existingPrimary.ifPresent(pref -> {
                pref.setIsPrimary(false);
                preferenceRepository.save(pref);
            });
        }

        PassengerPreference preference = PassengerPreference.builder()
                .passenger(passenger)
                .route(route)
                .stop(stop)
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .description(request.getDescription())
                .build();

        return preferenceRepository.save(preference);
    }

    @NonNull
    public List<PassengerPreference> getPassengerPreferences(Long passengerId)
    {
        return preferenceRepository.findByPassengerId(passengerId);
    }

    @NonNull
    public Optional<PassengerPreference> getPassengerPrimaryPreference(Long passengerId)
    {
        return preferenceRepository.findPrimaryPreferenceByPassengerId(passengerId);
    }

    public void deletePassengerPreference(Long preferenceId)
    {
        PassengerPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new IllegalArgumentException("偏好设置不存在"));

        preferenceRepository.delete(preference);
    }

    @NonNull
    public PassengerPreference setPrimaryPreference(Long preferenceId)
    {
        PassengerPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new IllegalArgumentException("偏好设置不存在"));

        Optional<PassengerPreference> existingPrimary =
                preferenceRepository.findPrimaryPreferenceByPassengerId(preference.getPassenger().getId());
        existingPrimary.ifPresent(pref -> {
            if (!pref.getId().equals(preferenceId))
            {
                pref.setIsPrimary(false);
                preferenceRepository.save(pref);
            }
        });

        preference.setIsPrimary(true);
        return preferenceRepository.save(preference);
    }

    @NonNull
    public List<PassengerPreference> getPassengersByRoute(Long routeId)
    {
        return preferenceRepository.findByRouteId(routeId);
    }

    @NonNull
    public List<PassengerPreference> getPassengersByStop(Long stopId)
    {
        return preferenceRepository.findByStopId(stopId);
    }
}
