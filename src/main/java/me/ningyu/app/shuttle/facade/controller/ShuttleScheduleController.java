package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.Shift;
import me.ningyu.app.shuttle.facade.service.ShuttleScheduleService;
import me.ningyu.app.shuttle.model.schedule.CreateScheduleRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateScheduleRequest;
import me.ningyu.app.shuttle.model.shift.ApproveShiftSwapRequest;
import me.ningyu.app.shuttle.model.shift.ShiftSwapRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "排班管理")
public class ShuttleScheduleController
{
    private final ShuttleScheduleService scheduleService;


    @PostMapping
    @Operation(summary = "创建排班计划", description = "支持批量，仅限今天及未来")
    public ResponseEntity<?> create(@Valid @RequestBody CreateScheduleRequest request)
    {
        List<Shift> schedules = scheduleService.createBulkSchedules(request);
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑排班计划", description = "适用于调班等场景，仅限修改今天及未来的计划")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "排班计划ID", example = "AARMmgYI_AAY", schema = @Schema(type = "long")),
            @Parameter(ref = "#/components/parameters/apiVersion")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateScheduleRequest request)
    {
        Shift updated = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除排班计划", description = "仅限取消今天及未来的排班，且无乘车记录")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "排班计划ID", example = "AARMmgYI_AAY", schema = @Schema(type = "long")),
            @Parameter(ref = "#/components/parameters/apiVersion")
    })
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        scheduleService.cancelSchedule(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // ==================== 调班相关功能 ====================

    @PostMapping("/shift-swap")
    @Operation(summary = "申请调班", description = "司机发起调班申请（需审批）或运营管理员直接调班（无需审批）")
    public ResponseEntity<Shift> requestShiftSwap(@Valid @RequestBody ShiftSwapRequest request)
    {
        Shift shift = scheduleService.requestShiftSwap(request);
        return ResponseEntity.ok(shift);
    }

    @PostMapping("/shift-swap/approve")
    @Operation(summary = "审批调班申请", description = "运营管理员审批司机发起的调班申请")
    public ResponseEntity<Shift> approveShiftSwap(@Valid @RequestBody ApproveShiftSwapRequest request)
    {
        Shift shift = scheduleService.approveShiftSwap(request);
        return ResponseEntity.ok(shift);
    }

    @GetMapping("/shift-swap/pending")
    @Operation(summary = "查询待审批的调班申请", description = "获取所有待审批状态的调班申请列表")
    public ResponseEntity<List<Shift>> getPendingShiftSwaps()
    {
        List<Shift> shifts = scheduleService.getPendingShiftSwaps();
        return ResponseEntity.ok(shifts);
    }
}
