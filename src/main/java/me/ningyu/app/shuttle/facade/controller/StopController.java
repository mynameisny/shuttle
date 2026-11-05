package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.facade.service.StopService;
import me.ningyu.app.shuttle.model.stop.CreateStopRequest;
import me.ningyu.app.shuttle.model.stop.StopUsageSummaryView;
import me.ningyu.app.shuttle.model.stop.StopUsageView;
import me.ningyu.app.shuttle.model.stop.UpdateStopRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "停靠点管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/stops")
@Slf4j
@RequiredArgsConstructor
public class StopController
{
    private final StopService stopService;

    @PostMapping
    @Operation(summary = "创建停靠点", description = "创建新站点（对应GTFS stops.txt），不传stop_id时自动生成")
    public ResponseEntity<Stop> create(@Valid @RequestBody CreateStopRequest request)
    {
        Stop stop = stopService.createStop(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(stop.getId())
                .toUri();
        return ResponseEntity.created(location).body(stop);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新停靠点", description = "更新站点名称与坐标（stop_id 不可修改）；坐标修正会同步影响所有引用该站点的班次")
    public ResponseEntity<Stop> update(
            @Parameter(description = "站点数据库主键") @PathVariable Long id,
            @Valid @RequestBody UpdateStopRequest request)
    {
        return ResponseEntity.ok(stopService.updateStop(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取停靠点详情", description = "按数据库主键查询单个站点（管理端站点维护页选中定位兜底：本地台账数据未同步时按需拉取）")
    public ResponseEntity<Stop> getById(
            @Parameter(description = "站点数据库主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(stopService.getStopById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除停靠点", description = "删除无引用的站点；被班次停站引用时返回 409 拒绝（GTFS 引用完整性）")
    public ResponseEntity<Void> delete(
            @Parameter(description = "站点数据库主键") @PathVariable Long id)
    {
        stopService.deleteStop(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usage-summary")
    @Operation(summary = "站点引用摘要", description = "批量返回各站点被线路/方向引用的摘要（同线路同方向去重）；无引用站点不出现在结果中，用于列表消歧与孤儿站识别")
    public ResponseEntity<List<StopUsageSummaryView>> usageSummary()
    {
        return ResponseEntity.ok(stopService.getUsageSummary());
    }

    @GetMapping("/{id}/usage")
    @Operation(summary = "查询站点引用", description = "反查引用该站点的班次及其所属线路（基于stop_times），按线路+方向+站序排序，用于展示修改站点的影响面")
    public ResponseEntity<List<StopUsageView>> usage(
            @Parameter(description = "站点数据库主键") @PathVariable Long id)
    {
        return ResponseEntity.ok(stopService.getStopUsage(id));
    }

    @GetMapping
    @Operation(summary = "获取停靠点列表", description = "获取所有站点，可按名称关键字模糊筛选")
    @Parameter(name = "keyword", description = "站点名称关键字（模糊匹配）", required = false)
    public ResponseEntity<List<Stop>> getAll(@RequestParam(required = false) String keyword)
    {
        return ResponseEntity.ok(stopService.getStops(keyword));
    }
}
