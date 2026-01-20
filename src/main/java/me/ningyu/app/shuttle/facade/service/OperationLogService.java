package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.OperationLogEntry;
import me.ningyu.app.shuttle.domain.entity.PhysicalStop;
import me.ningyu.app.shuttle.domain.entity.Shift;
import me.ningyu.app.shuttle.domain.repo.OperationLogEntryRepository;
import me.ningyu.app.shuttle.domain.repo.PhysicalStopRepository;
import me.ningyu.app.shuttle.domain.repo.ShiftRepository;
import me.ningyu.app.shuttle.enums.GPSPriority;
import me.ningyu.app.shuttle.model.operation.CreateOperationLogRequest;
import me.ningyu.app.shuttle.model.operation.UpdateOperationLogRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 运营记录服务
 *
 * TODO: 集成移动端GPS上报功能
 * TODO: 实现基于GPS的站点到达检测逻辑
 * TODO: 实现GPS数据优先级融合算法
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OperationLogService
{
    private final OperationLogEntryRepository operationLogRepository;
    private final ShiftRepository shiftRepository;
    private final PhysicalStopRepository stopRepository;

    /**
     * 创建运营记录
     * 用于记录班次在各站点的实际运行时间
     */
    @NonNull
    public OperationLogEntry createOperationLog(CreateOperationLogRequest request)
    {
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        PhysicalStop stop = stopRepository.findById(request.getStopId())
                .orElseThrow(() -> new IllegalArgumentException("站点不存在"));

        // 验证到达时间不早于班次发车时间
        LocalDateTime shiftDepartureDateTime = LocalDateTime.of(
                shift.getOperatingDate(),
                shift.getDepartureTime()
        );
        if (request.getActualArrivalTime().isBefore(shiftDepartureDateTime))
        {
            throw new IllegalArgumentException("实际到达时间不能早于班次发车时间");
        }

        OperationLogEntry logEntry = OperationLogEntry.builder()
                .shift(shift)
                .stop(stop)
                .actualArrivalTime(request.getActualArrivalTime())
                .actualDepartureTime(request.getActualDepartureTime())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .gpsPriority(request.getGpsPriority() != null ? request.getGpsPriority() : GPSPriority.VEHICLE_AUTO)
                .source(request.getSource())
                .remark(request.getRemark())
                .build();

        // 如果未提供出发时间，使用默认逻辑（到达时间 + 1分钟）
        if (logEntry.getActualDepartureTime() == null)
        {
            logEntry.setActualArrivalTimeWithDefaultDeparture(request.getActualArrivalTime());
        }

        return operationLogRepository.save(logEntry);
    }

    /**
     * 更新运营记录
     */
    @NonNull
    public OperationLogEntry updateOperationLog(Long id, UpdateOperationLogRequest request)
    {
        OperationLogEntry logEntry = operationLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("运营记录不存在"));

        if (request.getActualArrivalTime() != null)
        {
            logEntry.setActualArrivalTime(request.getActualArrivalTime());
        }

        if (request.getActualDepartureTime() != null)
        {
            logEntry.setActualDepartureTime(request.getActualDepartureTime());
        }

        if (request.getLatitude() != null)
        {
            logEntry.setLatitude(request.getLatitude());
        }

        if (request.getLongitude() != null)
        {
            logEntry.setLongitude(request.getLongitude());
        }

        if (request.getRemark() != null)
        {
            logEntry.setRemark(request.getRemark());
        }

        return operationLogRepository.save(logEntry);
    }

    /**
     * 删除运营记录
     */
    public void deleteOperationLog(Long id)
    {
        OperationLogEntry logEntry = operationLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("运营记录不存在"));

        operationLogRepository.delete(logEntry);
    }

    /**
     * 获取运营记录详情
     */
    @NonNull
    public OperationLogEntry getOperationLogById(Long id)
    {
        return operationLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("运营记录不存在"));
    }

    /**
     * 查询某个班次的所有运营记录
     */
    @NonNull
    public List<OperationLogEntry> getOperationLogsByShift(Long shiftId)
    {
        return operationLogRepository.findByShiftIdOrderByActualArrivalTimeAsc(shiftId);
    }

    /**
     * 查询某个班次在某个站点的运营记录
     */
    @NonNull
    public List<OperationLogEntry> getOperationLogsByShiftAndStop(Long shiftId, Long stopId)
    {
        return operationLogRepository.findByShiftIdAndStopId(shiftId, stopId);
    }

    /**
     * 查询某一天的所有运营记录
     */
    @NonNull
    public List<OperationLogEntry> getOperationLogsByDate(LocalDate date)
    {
        return operationLogRepository.findByOperatingDate(date);
    }

    /**
     * 司机手动上报GPS位置（高优先级）
     * TODO: 对接班车端Android应用
     */
    @NonNull
    public OperationLogEntry reportDriverGPS(Long shiftId, Long stopId, LocalDateTime arrivalTime,
                                              BigDecimal latitude, BigDecimal longitude, String remark)
    {
        CreateOperationLogRequest request = CreateOperationLogRequest.builder()
                .shiftId(shiftId)
                .stopId(stopId)
                .actualArrivalTime(arrivalTime)
                .latitude(latitude)
                .longitude(longitude)
                .gpsPriority(GPSPriority.DRIVER_MANUAL)
                .source("司机手动上报")
                .remark(remark)
                .build();

        log.info("司机手动上报GPS - 班次ID: {}, 站点ID: {}, 位置: ({}, {})",
                shiftId, stopId, latitude, longitude);

        return createOperationLog(request);
    }

    /**
     * 乘客扫码触发GPS记录（中等优先级）
     * TODO: 对接乘客端App扫码功能
     */
    @NonNull
    public OperationLogEntry reportPassengerScanGPS(Long shiftId, Long stopId, LocalDateTime scanTime,
                                                     BigDecimal latitude, BigDecimal longitude)
    {
        CreateOperationLogRequest request = CreateOperationLogRequest.builder()
                .shiftId(shiftId)
                .stopId(stopId)
                .actualArrivalTime(scanTime)
                .latitude(latitude)
                .longitude(longitude)
                .gpsPriority(GPSPriority.PASSENGER_SCAN)
                .source("乘客扫码")
                .build();

        log.info("乘客扫码触发GPS记录 - 班次ID: {}, 站点ID: {}, 位置: ({}, {})",
                shiftId, stopId, latitude, longitude);

        return createOperationLog(request);
    }

    /**
     * 车机自动上报GPS（低优先级）
     * TODO: 对接车机定时上报
     */
    @NonNull
    public OperationLogEntry reportVehicleAutoGPS(Long shiftId, Long stopId, LocalDateTime reportTime,
                                                   BigDecimal latitude, BigDecimal longitude)
    {
        CreateOperationLogRequest request = CreateOperationLogRequest.builder()
                .shiftId(shiftId)
                .stopId(stopId)
                .actualArrivalTime(reportTime)
                .latitude(latitude)
                .longitude(longitude)
                .gpsPriority(GPSPriority.VEHICLE_AUTO)
                .source("车机自动上报")
                .build();

        return createOperationLog(request);
    }
}
