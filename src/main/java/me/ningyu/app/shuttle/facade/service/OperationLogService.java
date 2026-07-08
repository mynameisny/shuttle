package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.OperationLogEntry;
import me.ningyu.app.shuttle.domain.entity.Stop;
import me.ningyu.app.shuttle.domain.entity.Trip;
import me.ningyu.app.shuttle.domain.repo.OperationLogEntryRepository;
import me.ningyu.app.shuttle.domain.repo.StopRepository;
import me.ningyu.app.shuttle.domain.repo.TripRepository;
import me.ningyu.app.shuttle.model.operation.CreateOperationLogRequest;
import me.ningyu.app.shuttle.model.operation.UpdateOperationLogRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OperationLogService
{
    private final OperationLogEntryRepository operationLogRepository;
    private final TripRepository tripRepository;
    private final StopRepository stopRepository;

    @NonNull
    public OperationLogEntry createOperationLog(CreateOperationLogRequest request)
    {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new IllegalArgumentException("班次不存在"));

        Stop stop = stopRepository.findById(request.getStopId())
                .orElseThrow(() -> new IllegalArgumentException("停靠点不存在"));

        OperationLogEntry logEntry = OperationLogEntry.builder()
                .trip(trip)
                .stop(stop)
                .actualArrivalTime(request.getActualArrivalTime())
                .actualDepartureTime(request.getActualDepartureTime())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .source(request.getSource())
                .remark(request.getRemark())
                .build();

        if (logEntry.getActualDepartureTime() == null)
        {
            logEntry.setActualArrivalTimeWithDefaultDeparture(request.getActualArrivalTime());
        }

        return operationLogRepository.save(logEntry);
    }

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

    public void deleteOperationLog(Long id)
    {
        OperationLogEntry logEntry = operationLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("运营记录不存在"));

        operationLogRepository.delete(logEntry);
    }

    @NonNull
    public OperationLogEntry getOperationLogById(Long id)
    {
        return operationLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("运营记录不存在"));
    }

    @NonNull
    public List<OperationLogEntry> getOperationLogsByTrip(Long tripId)
    {
        return operationLogRepository.findByTripIdOrderByActualArrivalTimeAsc(tripId);
    }

    @NonNull
    public List<OperationLogEntry> getOperationLogsByTripAndStop(Long tripId, Long stopId)
    {
        return operationLogRepository.findByTripIdAndStopId(tripId, stopId);
    }

    @NonNull
    public List<OperationLogEntry> getOperationLogsByDate(LocalDate date)
    {
        return operationLogRepository.findByOperatingDate(date);
    }

    @NonNull
    public OperationLogEntry reportDriverGPS(Long tripId, Long stopId, LocalDateTime arrivalTime,
                                              BigDecimal latitude, BigDecimal longitude, String remark)
    {
        CreateOperationLogRequest request = CreateOperationLogRequest.builder()
                .tripId(tripId)
                .stopId(stopId)
                .actualArrivalTime(arrivalTime)
                .latitude(latitude)
                .longitude(longitude)
                .source("司机手动上报")
                .remark(remark)
                .build();

        log.info("司机手动上报GPS - 班次ID: {}, 停靠点ID: {}, 位置: ({}, {})",
                tripId, stopId, latitude, longitude);

        return createOperationLog(request);
    }

    @NonNull
    public OperationLogEntry reportPassengerScanGPS(Long tripId, Long stopId, LocalDateTime scanTime,
                                                     BigDecimal latitude, BigDecimal longitude)
    {
        CreateOperationLogRequest request = CreateOperationLogRequest.builder()
                .tripId(tripId)
                .stopId(stopId)
                .actualArrivalTime(scanTime)
                .latitude(latitude)
                .longitude(longitude)
                .source("乘客扫码")
                .build();

        log.info("乘客扫码触发GPS记录 - 班次ID: {}, 停靠点ID: {}, 位置: ({}, {})",
                tripId, stopId, latitude, longitude);

        return createOperationLog(request);
    }

    @NonNull
    public OperationLogEntry reportVehicleAutoGPS(Long tripId, Long stopId, LocalDateTime reportTime,
                                                   BigDecimal latitude, BigDecimal longitude)
    {
        CreateOperationLogRequest request = CreateOperationLogRequest.builder()
                .tripId(tripId)
                .stopId(stopId)
                .actualArrivalTime(reportTime)
                .latitude(latitude)
                .longitude(longitude)
                .source("车机自动上报")
                .build();

        return createOperationLog(request);
    }
}
