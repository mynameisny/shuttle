package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
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
public class ShuttleScheduleService
{
    private final ShuttleScheduleRepository scheduleRepository;

    private final RouteDirectionRepository routeDirectionRepository;

    private final DriverRepository driverRepository;

    private final VehicleRepository vehicleRepository;

    private final RouteDirectionStopRepository routeDirectionStopRepository;

    private final RideRepository rideRepository;


    /**
     * 批量创建排班计划
     */
    @NonNull
    public List<ShuttleSchedule> createBulkSchedules(CreateScheduleRequest request)
    {
        RouteDirection routeDirection = routeDirectionRepository.findById(request.getRouteDirectionId()).orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));
        Driver driver = driverRepository.findById(request.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在"));

        // 获取该方向首站的发车时间（作为班次发车时间）
        LocalTime departureTime = getFirstStopDepartureTime(routeDirection);

        List<LocalDate> dates = generateDates(request.getStartDate(), request.getEndDate(), request.getIncludeWeekends());
        List<ShuttleSchedule> schedules = new ArrayList<>();

        for (LocalDate date : dates)
        {
            // 校验冲突
            validateNoConflict(driver.getId(), vehicle.getId(), routeDirection.getId(), date, departureTime);

            ShuttleSchedule schedule = ShuttleSchedule.builder()
                    .routeDirection(routeDirection)
                    .driver(driver)
                    .vehicle(vehicle)
                    .operatingDate(date)
                    .departureTime(departureTime)
                    .active(true)
                    .build();
            schedules.add(schedule);
        }

        return scheduleRepository.saveAll(schedules);
    }

    /**
     * 编辑单个班次（调班）
     */
    public ShuttleSchedule updateSchedule(Long scheduleId, UpdateScheduleRequest request)
    {
        if (!request.getOperatingDate().isAfter(LocalDate.now()) && !request.getOperatingDate().isEqual(LocalDate.now()))
        {
            throw new IllegalArgumentException("只能编辑今天及未来的排班");
        }

        ShuttleSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("班次不存在"));
        Driver driver = driverRepository.findById(request.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在"));

        // 校验冲突（排除当前班次自身）
        validateNoConflictExceptSelf(driver.getId(), vehicle.getId(), schedule.getRouteDirection().getId(), request.getOperatingDate(), request.getDepartureTime(), scheduleId);

        schedule.setDriver(driver);
        schedule.setVehicle(vehicle);
        schedule.setOperatingDate(request.getOperatingDate());
        schedule.setDepartureTime(request.getDepartureTime());

        return scheduleRepository.save(schedule);
    }

    /**
     * 取消（删除）指定班次
     * 仅允许取消今天及未来的班次，且不能有乘车记录
     */
    public void cancelSchedule(Long scheduleId)
    {
        ShuttleSchedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        // 检查是否为历史班次
        if (schedule.getOperatingDate().isBefore(LocalDate.now()))
        {
            throw new IllegalArgumentException("不能取消历史排班");
        }

        // 检查是否有乘车记录（防止数据不一致）
        if (rideRepository.existsByScheduleId(scheduleId))
        {
            throw new IllegalStateException("该班次已有乘客记录，不能取消");
        }

        scheduleRepository.deleteById(scheduleId);
    }

    // ========== 内部工具方法 ==========

    private void validateDateRange(LocalDate start, LocalDate end)
    {
        if (start.isBefore(LocalDate.now()))
        {
            throw new IllegalArgumentException("开始日期不能早于今天");
        }
        if (end.isBefore(start))
        {
            throw new IllegalArgumentException("结束日期不能早于开始日期");
        }
        if (start.plusMonths(3).isBefore(end))
        {
            throw new IllegalArgumentException("排班范围不能超过3个月");
        }
    }

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

    private LocalTime getFirstStopDepartureTime(RouteDirection routeDirection)
    {
        return routeDirectionStopRepository.findFirstByRouteDirectionOrderBySequenceAsc(routeDirection)
                .map(RouteDirectionStop::getDepartureTime)
                .orElseThrow(() -> new IllegalStateException("线路方向未配置站点发车时间"));
    }

    private void validateNoConflict(Long driverId, Long vehicleId, Long routeDirectionId, LocalDate date, LocalTime departureTime)
    {
        // 检查司机冲突
        if (scheduleRepository.existsByDriverIdAndOperatingDateAndTimeOverlap(driverId, date, departureTime))
        {
            throw new IllegalArgumentException("该司机在指定时间已有其他排班");
        }
        // 检查车辆冲突
        if (scheduleRepository.existsByVehicleIdAndOperatingDateAndTimeOverlap(vehicleId, date, departureTime))
        {
            throw new IllegalArgumentException("该车辆在指定时间已被其他班次占用");
        }
    }

    private void validateNoConflictExceptSelf(Long driverId, Long vehicleId, Long routeDirectionId, LocalDate date, LocalTime departureTime, Long excludeScheduleId)
    {
        if (scheduleRepository.existsByDriverIdAndOperatingDateAndTimeOverlapExcludingId(driverId, date, departureTime, excludeScheduleId))
        {
            throw new IllegalArgumentException("该司机在指定时间已有其他排班");
        }
        if (scheduleRepository.existsByVehicleIdAndOperatingDateAndTimeOverlapExcludingId(vehicleId, date, departureTime, excludeScheduleId))
        {
            throw new IllegalArgumentException("该车辆在指定时间已被其他班次占用");
        }
    }
}
