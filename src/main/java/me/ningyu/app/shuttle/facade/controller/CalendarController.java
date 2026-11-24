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
import me.ningyu.app.shuttle.domain.entity.gtfs.CalendarDate;
import me.ningyu.app.shuttle.facade.service.CalendarService;
import me.ningyu.app.shuttle.model.calendar.AddCalendarDateRequest;
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

    // ---------- 例外日期（calendar_dates：法定假日 / 调休 / 临时停运） ----------

    @GetMapping("/{id}/dates")
    @Operation(summary = "某日历的例外日期列表")
    public ResponseEntity<List<CalendarDate>> listDates(
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "日历ID", schema = @Schema(type = "long"))
            @PathVariable Long id)
    {
        return ResponseEntity.ok(calendarService.listCalendarDates(id));
    }

    @PostMapping("/{id}/dates")
    @Operation(summary = "添加例外日期（法定假日停运 / 调休加开）")
    public ResponseEntity<CalendarDate> addDate(
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "日历ID", schema = @Schema(type = "long"))
            @PathVariable Long id,
            @Valid @RequestBody AddCalendarDateRequest request)
    {
        return ResponseEntity.ok(calendarService.addCalendarDate(id, request));
    }

    @PutMapping("/{id}/dates")
    @Operation(summary = "全量替换例外日期", description = "以请求列表为目标态整体替换某日历的例外（日历勾选 / JSON 批量导入的统一保存入口）")
    public ResponseEntity<List<CalendarDate>> replaceDates(
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "日历ID", schema = @Schema(type = "long"))
            @PathVariable Long id,
            @RequestBody List<AddCalendarDateRequest> requests)
    {
        return ResponseEntity.ok(calendarService.replaceCalendarDates(id, requests));
    }

    @DeleteMapping("/dates/{dateId}")
    @Operation(summary = "删除例外日期")
    public ResponseEntity<Void> deleteDate(
            @Parameter(name = "dateId", in = ParameterIn.PATH, required = true, description = "例外日期主键", schema = @Schema(type = "long"))
            @PathVariable Long dateId)
    {
        calendarService.deleteCalendarDate(dateId);
        return ResponseEntity.noContent().build();
    }
}
