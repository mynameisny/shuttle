package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.Trip;
import me.ningyu.app.shuttle.facade.service.TripService;
import me.ningyu.app.shuttle.model.schedule.CreateScheduleRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateScheduleRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "班次管理")
public class TripController
{
    private final TripService tripService;

    @PostMapping
    @Operation(summary = "创建班次计划", description = "支持批量创建，仅限今天及未来")
    public ResponseEntity<List<Trip>> create(@Valid @RequestBody CreateScheduleRequest request)
    {
        List<Trip> trips = tripService.createBulkTrips(request);
        return ResponseEntity.ok(trips);
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑班次", description = "适用于调班等场景，仅限修改今天及未来的班次")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Trip> update(@PathVariable Long id, @Valid @RequestBody UpdateScheduleRequest request)
    {
        Trip updated = tripService.updateTrip(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "取消班次", description = "仅限取消今天及未来的班次，且无乘车记录")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        tripService.cancelTrip(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取班次详情", description = "根据ID获取班次的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Trip> getById(@PathVariable Long id)
    {
        Trip trip = tripService.getTripById(id);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/by-date/{date}")
    @Operation(summary = "根据日期查询班次", description = "获取指定日期的所有班次")
    @Parameters({
            @Parameter(name = "date", in = ParameterIn.PATH, required = true, description = "日期（格式：yyyy-MM-dd）", schema = @Schema(type = "string"))
    })
    public ResponseEntity<List<Trip>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        List<Trip> trips = tripService.getTripsByDate(date);
        return ResponseEntity.ok(trips);
    }
}
