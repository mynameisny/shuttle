package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.OperationLogEntry;
import me.ningyu.app.shuttle.facade.service.OperationLogService;
import me.ningyu.app.shuttle.model.operation.CreateOperationLogRequest;
import me.ningyu.app.shuttle.model.operation.UpdateOperationLogRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 运营记录 Controller
 * 用于记录班次实际运行数据
 */
@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
@Tag(name = "运营记录管理", description = "班次实际运行数据记录和查询")
public class OperationLogController
{
    private final OperationLogService operationLogService;

    @PostMapping
    @Operation(summary = "创建运营记录", description = "记录班次在站点的实际到达/出发时间和GPS位置")
    public ResponseEntity<OperationLogEntry> createOperationLog(
            @Valid @RequestBody CreateOperationLogRequest request)
    {
        OperationLogEntry logEntry = operationLogService.createOperationLog(request);
        return ResponseEntity.ok(logEntry);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新运营记录")
    @Parameter(name = "id", description = "运营记录ID", required = true)
    public ResponseEntity<OperationLogEntry> updateOperationLog(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOperationLogRequest request)
    {
        OperationLogEntry logEntry = operationLogService.updateOperationLog(id, request);
        return ResponseEntity.ok(logEntry);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除运营记录")
    @Parameter(name = "id", description = "运营记录ID", required = true)
    public ResponseEntity<Void> deleteOperationLog(@PathVariable Long id)
    {
        operationLogService.deleteOperationLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取运营记录详情")
    @Parameter(name = "id", description = "运营记录ID", required = true)
    public ResponseEntity<OperationLogEntry> getOperationLog(@PathVariable Long id)
    {
        OperationLogEntry logEntry = operationLogService.getOperationLogById(id);
        return ResponseEntity.ok(logEntry);
    }

    @GetMapping("/by-shift/{shiftId}")
    @Operation(summary = "查询某个班次的所有运营记录", description = "按到达时间升序排列")
    @Parameter(name = "shiftId", description = "班次ID", required = true)
    public ResponseEntity<List<OperationLogEntry>> getOperationLogsByShift(@PathVariable Long shiftId)
    {
        List<OperationLogEntry> logs = operationLogService.getOperationLogsByShift(shiftId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/by-shift-and-stop")
    @Operation(summary = "查询某个班次在某个站点的运营记录")
    public ResponseEntity<List<OperationLogEntry>> getOperationLogsByShiftAndStop(
            @RequestParam @Parameter(description = "班次ID", required = true) Long shiftId,
            @RequestParam @Parameter(description = "站点ID", required = true) Long stopId)
    {
        List<OperationLogEntry> logs = operationLogService.getOperationLogsByShiftAndStop(shiftId, stopId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/by-date/{date}")
    @Operation(summary = "查询某一天的所有运营记录")
    @Parameter(name = "date", description = "日期（格式：yyyy-MM-dd）", required = true, example = "2026-01-20")
    public ResponseEntity<List<OperationLogEntry>> getOperationLogsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        List<OperationLogEntry> logs = operationLogService.getOperationLogsByDate(date);
        return ResponseEntity.ok(logs);
    }
}
