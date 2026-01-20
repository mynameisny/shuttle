package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Passenger;
import me.ningyu.app.shuttle.domain.entity.PassengerPreference;
import me.ningyu.app.shuttle.domain.entity.PhysicalStop;
import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import me.ningyu.app.shuttle.domain.repo.PassengerPreferenceRepository;
import me.ningyu.app.shuttle.domain.repo.PassengerRepository;
import me.ningyu.app.shuttle.domain.repo.PhysicalStopRepository;
import me.ningyu.app.shuttle.domain.repo.RouteDirectionRepository;
import me.ningyu.app.shuttle.model.passenger.CreatePassengerPreferenceRequest;
import me.ningyu.app.shuttle.model.passenger.CreatePassengerRequest;
import me.ningyu.app.shuttle.model.passenger.UpdatePassengerRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 乘客管理服务
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PassengerService
{
    private final PassengerRepository passengerRepository;
    private final PassengerPreferenceRepository preferenceRepository;
    private final RouteDirectionRepository routeDirectionRepository;
    private final PhysicalStopRepository stopRepository;

    /**
     * 创建乘客
     */
    @NonNull
    public Passenger createPassenger(CreatePassengerRequest request)
    {
        // 检查员工编号是否已存在
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

    /**
     * 更新乘客信息
     */
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

    /**
     * 删除乘客
     */
    public void deletePassenger(Long id)
    {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));

        // 删除乘客时，级联删除其偏好设置（orphanRemoval = true）
        passengerRepository.delete(passenger);
    }

    /**
     * 获取乘客详情
     */
    @NonNull
    public Passenger getPassengerById(Long id)
    {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));
    }

    /**
     * 根据员工编号查询乘客
     */
    @NonNull
    public Optional<Passenger> getPassengerByEmployeeNumber(String employeeNumber)
    {
        return passengerRepository.findByEmployeeNumber(employeeNumber);
    }

    /**
     * 获取所有乘客列表
     */
    @NonNull
    public List<Passenger> getAllPassengers()
    {
        return passengerRepository.findAll();
    }

    /**
     * 为乘客添加偏好设置
     */
    @NonNull
    public PassengerPreference addPassengerPreference(CreatePassengerPreferenceRequest request)
    {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("乘客不存在"));

        RouteDirection routeDirection = routeDirectionRepository.findById(request.getRouteDirectionId())
                .orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));

        PhysicalStop stop = stopRepository.findById(request.getStopId())
                .orElseThrow(() -> new IllegalArgumentException("站点不存在"));

        // 如果设置为主要偏好，需要先取消其他主要偏好
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
                .routeDirection(routeDirection)
                .stop(stop)
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .description(request.getDescription())
                .build();

        return preferenceRepository.save(preference);
    }

    /**
     * 获取乘客的所有偏好设置
     */
    @NonNull
    public List<PassengerPreference> getPassengerPreferences(Long passengerId)
    {
        return preferenceRepository.findByPassengerId(passengerId);
    }

    /**
     * 获取乘客的主要偏好
     */
    @NonNull
    public Optional<PassengerPreference> getPassengerPrimaryPreference(Long passengerId)
    {
        return preferenceRepository.findPrimaryPreferenceByPassengerId(passengerId);
    }

    /**
     * 删除偏好设置
     */
    public void deletePassengerPreference(Long preferenceId)
    {
        PassengerPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new IllegalArgumentException("偏好设置不存在"));

        preferenceRepository.delete(preference);
    }

    /**
     * 设置主要偏好
     */
    @NonNull
    public PassengerPreference setPrimaryPreference(Long preferenceId)
    {
        PassengerPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new IllegalArgumentException("偏好设置不存在"));

        // 取消该乘客的其他主要偏好
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

    /**
     * 查询某线路的常乘客列表
     */
    @NonNull
    public List<PassengerPreference> getPassengersByRouteDirection(Long routeDirectionId)
    {
        return preferenceRepository.findByRouteDirectionId(routeDirectionId);
    }

    /**
     * 查询某站点的常乘客列表
     */
    @NonNull
    public List<PassengerPreference> getPassengersByStop(Long stopId)
    {
        return preferenceRepository.findByStopId(stopId);
    }
}
