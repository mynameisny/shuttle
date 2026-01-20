package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.Passenger;
import me.ningyu.app.shuttle.domain.entity.PassengerPreference;
import me.ningyu.app.shuttle.facade.service.PassengerService;
import me.ningyu.app.shuttle.model.passenger.CreatePassengerPreferenceRequest;
import me.ningyu.app.shuttle.model.passenger.CreatePassengerRequest;
import me.ningyu.app.shuttle.model.passenger.UpdatePassengerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 乘客管理 Controller
 */
@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Tag(name = "乘客管理", description = "乘客信息及偏好管理")
public class PassengerController
{
    private final PassengerService passengerService;

    @PostMapping
    @Operation(summary = "创建乘客", description = "新增乘客信息")
    public ResponseEntity<Passenger> createPassenger(@Valid @RequestBody CreatePassengerRequest request)
    {
        Passenger passenger = passengerService.createPassenger(request);
        return ResponseEntity.ok(passenger);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新乘客信息")
    @Parameter(name = "id", description = "乘客ID", required = true)
    public ResponseEntity<Passenger> updatePassenger(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePassengerRequest request)
    {
        Passenger passenger = passengerService.updatePassenger(id, request);
        return ResponseEntity.ok(passenger);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除乘客")
    @Parameter(name = "id", description = "乘客ID", required = true)
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id)
    {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取乘客详情")
    @Parameter(name = "id", description = "乘客ID", required = true)
    public ResponseEntity<Passenger> getPassenger(@PathVariable Long id)
    {
        Passenger passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }

    @GetMapping
    @Operation(summary = "获取所有乘客列表")
    public ResponseEntity<List<Passenger>> getAllPassengers()
    {
        List<Passenger> passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/by-employee-number/{employeeNumber}")
    @Operation(summary = "根据员工编号查询乘客")
    @Parameter(name = "employeeNumber", description = "员工编号", required = true)
    public ResponseEntity<Passenger> getPassengerByEmployeeNumber(@PathVariable String employeeNumber)
    {
        return passengerService.getPassengerByEmployeeNumber(employeeNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== 乘客偏好管理 ====================

    @PostMapping("/preferences")
    @Operation(summary = "添加乘客偏好设置", description = "为乘客添加常坐线路和站点")
    public ResponseEntity<PassengerPreference> addPassengerPreference(
            @Valid @RequestBody CreatePassengerPreferenceRequest request)
    {
        PassengerPreference preference = passengerService.addPassengerPreference(request);
        return ResponseEntity.ok(preference);
    }

    @GetMapping("/{passengerId}/preferences")
    @Operation(summary = "获取乘客的所有偏好设置")
    @Parameter(name = "passengerId", description = "乘客ID", required = true)
    public ResponseEntity<List<PassengerPreference>> getPassengerPreferences(@PathVariable Long passengerId)
    {
        List<PassengerPreference> preferences = passengerService.getPassengerPreferences(passengerId);
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/{passengerId}/preferences/primary")
    @Operation(summary = "获取乘客的主要偏好")
    @Parameter(name = "passengerId", description = "乘客ID", required = true)
    public ResponseEntity<PassengerPreference> getPassengerPrimaryPreference(@PathVariable Long passengerId)
    {
        return passengerService.getPassengerPrimaryPreference(passengerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/preferences/{preferenceId}")
    @Operation(summary = "删除偏好设置")
    @Parameter(name = "preferenceId", description = "偏好设置ID", required = true)
    public ResponseEntity<Void> deletePassengerPreference(@PathVariable Long preferenceId)
    {
        passengerService.deletePassengerPreference(preferenceId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/preferences/{preferenceId}/set-primary")
    @Operation(summary = "设置为主要偏好", description = "设置某个偏好为主要偏好，用于满载率分析")
    @Parameter(name = "preferenceId", description = "偏好设置ID", required = true)
    public ResponseEntity<PassengerPreference> setPrimaryPreference(@PathVariable Long preferenceId)
    {
        PassengerPreference preference = passengerService.setPrimaryPreference(preferenceId);
        return ResponseEntity.ok(preference);
    }

    // ==================== 统计查询 ====================

    @GetMapping("/by-route-direction/{routeDirectionId}")
    @Operation(summary = "查询某线路的常乘客列表", description = "用于满载率统计")
    @Parameter(name = "routeDirectionId", description = "线路方向ID", required = true)
    public ResponseEntity<List<PassengerPreference>> getPassengersByRouteDirection(@PathVariable Long routeDirectionId)
    {
        List<PassengerPreference> preferences = passengerService.getPassengersByRouteDirection(routeDirectionId);
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/by-stop/{stopId}")
    @Operation(summary = "查询某站点的常乘客列表")
    @Parameter(name = "stopId", description = "站点ID", required = true)
    public ResponseEntity<List<PassengerPreference>> getPassengersByStop(@PathVariable Long stopId)
    {
        List<PassengerPreference> preferences = passengerService.getPassengersByStop(stopId);
        return ResponseEntity.ok(preferences);
    }
}
