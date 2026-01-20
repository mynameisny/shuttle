package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.*;
import me.ningyu.app.shuttle.domain.repo.*;
import me.ningyu.app.shuttle.enums.ShiftSwapStatus;
import me.ningyu.app.shuttle.model.schedule.CreateScheduleRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateScheduleRequest;
import me.ningyu.app.shuttle.model.shift.ApproveShiftSwapRequest;
import me.ningyu.app.shuttle.model.shift.ShiftSwapRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ShuttleScheduleService
{
    private final ShiftRepository shiftRepository;

    private final RouteDirectionRepository routeDirectionRepository;

    private final DriverRepository driverRepository;

    private final VehicleRepository vehicleRepository;

    private final RouteStopRepository routeStopRepository;

    private final RideRepository rideRepository;


    /**
     * 批量创建排班计划
     */
    public List<Shift> createBulkSchedules(CreateScheduleRequest request)
    {
        RouteDirection routeDirection = routeDirectionRepository.findById(request.getRouteDirectionId()).orElseThrow(() -> new IllegalArgumentException("线路方向不存在"));
        Driver driver = driverRepository.findById(request.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在"));

        // 获取该方向首站的发车时间（作为班次发车时间）
        LocalTime departureTime = getFirstStopDepartureTime(routeDirection);

        List<LocalDate> dates = generateDates(request.getStartDate(), request.getEndDate(), request.getIncludeWeekends());
        List<Shift> schedules = new ArrayList<>();

        for (LocalDate date : dates)
        {
            // 校验冲突
            validateNoConflict(driver.getId(), vehicle.getId(), routeDirection.getId(), date, departureTime);

            Shift schedule = Shift.builder()
                    .routeDirection(routeDirection)
                    .driver(driver)
                    .vehicle(vehicle)
                    .operatingDate(date)
                    .departureTime(departureTime)
                    .active(true)
                    .build();
            schedules.add(schedule);
        }

        return shiftRepository.saveAll(schedules);
    }

    /**
     * 编辑单个班次（调班）
     */
    public Shift updateSchedule(Long scheduleId, UpdateScheduleRequest request)
    {
        if (!request.getOperatingDate().isAfter(LocalDate.now()) && !request.getOperatingDate().isEqual(LocalDate.now()))
        {
            throw new IllegalArgumentException("只能编辑今天及未来的排班");
        }

        Shift schedule = shiftRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("班次不存在"));
        Driver driver = driverRepository.findById(request.getDriverId()).orElseThrow(() -> new IllegalArgumentException("司机不存在"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId()).orElseThrow(() -> new IllegalArgumentException("车辆不存在"));

        // 校验冲突（排除当前班次自身）
        validateNoConflictExceptSelf(driver.getId(), vehicle.getId(), schedule.getRouteDirection().getId(), request.getOperatingDate(), request.getDepartureTime(), scheduleId);

        schedule.setDriver(driver);
        schedule.setVehicle(vehicle);
        schedule.setOperatingDate(request.getOperatingDate());
        schedule.setDepartureTime(request.getDepartureTime());

        return shiftRepository.save(schedule);
    }

    /**
     * 取消（删除）指定班次
     * 仅允许取消今天及未来的班次，且不能有乘车记录
     */
    public void cancelSchedule(Long scheduleId)
    {
        Shift schedule = shiftRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("班次不存在"));

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

        shiftRepository.deleteById(scheduleId);
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
        return routeStopRepository.findFirstByRouteDirectionOrderBySequenceAsc(routeDirection)
                .map(RouteStop::getDepartureTime)
                .orElseThrow(() -> new IllegalStateException("线路方向未配置站点发车时间"));
    }

    private void validateNoConflict(Long driverId, Long vehicleId, Long routeDirectionId, LocalDate date, LocalTime departureTime)
    {
        // 检查司机冲突
        if (shiftRepository.existsByDriverIdAndOperatingDateAndTimeOverlap(driverId, date, departureTime))
        {
            throw new IllegalArgumentException("该司机在指定时间已有其他排班");
        }
        // 检查车辆冲突
        if (shiftRepository.existsByVehicleIdAndOperatingDateAndTimeOverlap(vehicleId, date, departureTime))
        {
            throw new IllegalArgumentException("该车辆在指定时间已被其他班次占用");
        }
    }

    private void validateNoConflictExceptSelf(Long driverId, Long vehicleId, Long routeDirectionId, LocalDate date, LocalTime departureTime, Long excludeScheduleId)
    {
        if (shiftRepository.existsByDriverIdAndOperatingDateAndTimeOverlapExcludingId(driverId, date, departureTime, excludeScheduleId))
        {
            throw new IllegalArgumentException("该司机在指定时间已有其他排班");
        }
        if (shiftRepository.existsByVehicleIdAndOperatingDateAndTimeOverlapExcludingId(vehicleId, date, departureTime, excludeScheduleId))
        {
            throw new IllegalArgumentException("该车辆在指定时间已被其他班次占用");
        }
    }

    // ==================== 调班相关功能 ====================

    /**
     * 申请调班
     * 司机发起调班申请，需要审批；运营管理员直接调班无需审批
     *
     * @param request 调班请求
     * @return 调班后的班次
     */
    @NonNull
    public Shift requestShiftSwap(ShiftSwapRequest request)
    {
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        Driver newDriver = driverRepository.findById(request.getNewDriverId())
                .orElseThrow(() -> new IllegalArgumentException("新司机不存在"));

        // 验证调班约束
        validateShiftSwap(shift, newDriver);

        // 记录原始司机
        if (shift.getOriginalDriver() == null)
        {
            shift.setOriginalDriver(shift.getDriver());
        }

        // 判断是否为管理员操作
        if (request.getIsAdminOperation() != null && request.getIsAdminOperation())
        {
            // 运营管理员直接调班，无需审批
            performShiftSwap(shift, newDriver, request.getReason(), null);
            log.info("运营管理员直接调班 - 班次ID: {}, 原司机: {}, 新司机: {}",
                    shift.getId(), shift.getOriginalDriver().getName(), newDriver.getName());
        }
        else
        {
            // 司机发起调班，需要审批
            shift.setSwapStatus(ShiftSwapStatus.PENDING_APPROVAL);
            shift.setSwapRequestedAt(LocalDateTime.now());
            shift.setSwapReason(request.getReason());

            // TODO: 集成 Camunda 工作流引擎，启动调班审批流程
            // String processInstanceId = camundaService.startShiftSwapProcess(shift.getId(), shift.getDriver().getId(), newDriver.getId());
            // shift.setWorkflowProcessInstanceId(processInstanceId);

            log.info("司机发起调班申请 - 班次ID: {}, 原司机: {}, 新司机: {}, 等待审批",
                    shift.getId(), shift.getDriver().getName(), newDriver.getName());
        }

        return shiftRepository.save(shift);
    }

    /**
     * 审批调班申请
     *
     * @param request 审批请求
     * @return 审批后的班次
     */
    @NonNull
    public Shift approveShiftSwap(ApproveShiftSwapRequest request)
    {
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        // 验证调班状态
        if (shift.getSwapStatus() != ShiftSwapStatus.PENDING_APPROVAL)
        {
            throw new IllegalStateException("当前班次不在待审批状态，无法审批");
        }

        if (request.getApproved())
        {
            // 批准调班 - 需要从班次中找到新司机ID（这里需要在 Shift 中临时保存新司机ID，或从工作流中获取）
            // 简化实现：假设在调班申请时已经将新司机设置到 driver 字段
            shift.setSwapStatus(ShiftSwapStatus.APPROVED);
            shift.setSwapApprovedAt(LocalDateTime.now());
            shift.setApproverId(request.getApproverId());

            log.info("调班申请已批准 - 班次ID: {}, 审批人ID: {}", shift.getId(), request.getApproverId());
        }
        else
        {
            // 拒绝调班 - 恢复为正常状态
            shift.setSwapStatus(ShiftSwapStatus.REJECTED);
            shift.setSwapApprovedAt(LocalDateTime.now());
            shift.setApproverId(request.getApproverId());

            // 恢复原司机（如果已更改）
            if (shift.getOriginalDriver() != null)
            {
                shift.setDriver(shift.getOriginalDriver());
            }

            log.info("调班申请已拒绝 - 班次ID: {}, 审批人ID: {}", shift.getId(), request.getApproverId());
        }

        // 记录审批备注
        if (request.getRemark() != null && !request.getRemark().isEmpty())
        {
            String updatedReason = shift.getSwapReason() != null
                    ? shift.getSwapReason() + "\n审批备注: " + request.getRemark()
                    : "审批备注: " + request.getRemark();
            shift.setSwapReason(updatedReason);
        }

        // TODO: 更新 Camunda 流程状态
        // camundaService.completeShiftSwapTask(shift.getWorkflowProcessInstanceId(), request.getApproved());

        return shiftRepository.save(shift);
    }

    /**
     * 验证调班约束
     */
    private void validateShiftSwap(Shift shift, Driver newDriver)
    {
        // 1. 调班只能在出发前操作
        LocalDateTime departureDateTime = LocalDateTime.of(shift.getOperatingDate(), shift.getDepartureTime());
        if (LocalDateTime.now().isAfter(departureDateTime))
        {
            throw new IllegalStateException("班次已出发，不能调班");
        }

        // 2. 新司机必须是空闲的（在同一时间没有其他排班）
        if (shiftRepository.existsByDriverIdAndOperatingDateAndTimeOverlap(
                newDriver.getId(),
                shift.getOperatingDate(),
                shift.getDepartureTime()))
        {
            throw new IllegalArgumentException("新司机在该时间已有其他排班，无法调班");
        }

        // 3. 验证驾照有效期（可选）
        DrivingLicense license = newDriver.getLicense();
        if (license != null)
        {
            // TODO: DrivingLicense 字段类型需要从 LocalTime 改为 LocalDate
            // 当前先注释掉，等 DrivingLicense 实体修复后再启用
            /*
            LocalDate today = LocalDate.now();
            if (license.getExpireTime() != null && license.getExpireTime().isBefore(today))
            {
                throw new IllegalArgumentException("新司机的驾照已过期，无法调班");
            }
            if (license.getIssueTime() != null && license.getIssueTime().isAfter(today))
            {
                throw new IllegalArgumentException("新司机的驾照尚未生效，无法调班");
            }
            */
        }
    }

    /**
     * 执行调班操作
     */
    private void performShiftSwap(Shift shift, Driver newDriver, String reason, Long approverId)
    {
        shift.setDriver(newDriver);
        shift.setSwapStatus(ShiftSwapStatus.APPROVED);
        shift.setSwapRequestedAt(LocalDateTime.now());
        shift.setSwapApprovedAt(LocalDateTime.now());
        shift.setSwapReason(reason);
        shift.setApproverId(approverId);
    }

    /**
     * 查询待审批的调班申请
     */
    @NonNull
    public List<Shift> getPendingShiftSwaps()
    {
        // TODO: 在 ShiftRepository 中添加查询方法
        // return shiftRepository.findBySwapStatus(ShiftSwapStatus.PENDING_APPROVAL);
        return new ArrayList<>();
    }
}
