package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Calendar;
import me.ningyu.app.shuttle.facade.service.CalendarService;
import me.ningyu.app.shuttle.model.calendar.CreateCalendarRequest;
import me.ningyu.app.shuttle.model.calendar.UpdateCalendarRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "服务日历管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/calendars")
@Slf4j
@RequiredArgsConstructor
public class CalendarController
{
    private final CalendarService calendarService;

    @PostMapping
    @Operation(summary = "创建服务日历", description = "创建一个新的服务日历（对应 GTFS calendar.txt）")
    public ResponseEntity<Calendar> create(@Valid @RequestBody CreateCalendarRequest request)
    {
        Calendar calendar = calendarService.createCalendar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(calendar.getId())
                .toUri();
        return ResponseEntity.created(location).body(calendar);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新服务日历", description = "更新指定服务日历（字段可选，非 null 才更新；service_id 不可改）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "日历ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Calendar> update(@PathVariable Long id, @Valid @RequestBody UpdateCalendarRequest request)
    {
        return ResponseEntity.ok(calendarService.updateCalendar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务日历", description = "删除指定服务日历")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "日历ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        calendarService.deleteCalendar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "获取服务日历列表", description = "获取所有服务日历，可按 serviceId 精确筛选")
    public ResponseEntity<List<Calendar>> getAll(@RequestParam(required = false) String serviceId)
    {
        if (serviceId != null && !serviceId.isBlank())
        {
            return ResponseEntity.ok(List.of(calendarService.getCalendarByServiceId(serviceId)));
        }
        return ResponseEntity.ok(calendarService.getAllCalendars());
    }

    @GetMapping("/active-on/{date}")
    @Operation(summary = "查询某日期生效的日历", description = "返回 startDate <= date <= endDate 的日历列表")
    public ResponseEntity<List<Calendar>> getActiveOn(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        return ResponseEntity.ok(calendarService.getCalendarsActiveOn(date));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取服务日历详情", description = "根据 ID 获取服务日历详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "日历ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Calendar> getById(@PathVariable Long id)
    {
        return ResponseEntity.ok(calendarService.getCalendarById(id));
    }
}
