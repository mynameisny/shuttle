package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.facade.service.StopEventService;
import me.ningyu.app.shuttle.model.fleet.ReportStopEventRequest;
import me.ningyu.app.shuttle.model.fleet.StopEventView;
import me.ningyu.app.shuttle.model.fleet.TripAnalysisView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "到发记录")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/stop-events")
@RequiredArgsConstructor
public class StopEventController
{
    private final StopEventService stopEventService;

    @GetMapping
    @Operation(summary = "到发记录列表（可按班次/日期过滤）")
    public ResponseEntity<List<StopEventView>> list(
            @Parameter(description = "班次 trip_id") @RequestParam(required = false) String tripId,
            @Parameter(description = "运营日期") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate eventDate)
    {
        return ResponseEntity.ok(stopEventService.list(tripId, eventDate));
    }

    @GetMapping("/analysis")
    @Operation(summary = "班次运营分析", description = "某班次在一段日期内的各运营日站点计划/实际到发明细与逐日准时率统计（±3 分钟为准时阈值，非运营日自动剔除）")
    public ResponseEntity<TripAnalysisView> analyze(
            @Parameter(description = "班次 trip_id") @RequestParam String tripId,
            @Parameter(description = "开始日期（含）") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期（含）") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        return ResponseEntity.ok(stopEventService.analyze(tripId, startDate, endDate));
    }

    @PostMapping
    @Operation(summary = "上报到站/发车（手动与设备共用）")
    public ResponseEntity<StopEventView> report(@Valid @RequestBody ReportStopEventRequest request)
    {
        return ResponseEntity.ok(stopEventService.report(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除到发记录")
    public ResponseEntity<Void> delete(@Parameter(description = "记录主键") @PathVariable Long id)
    {
        stopEventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
