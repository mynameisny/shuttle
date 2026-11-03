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

    @GetMapping
    @Operation(summary = "获取停靠点列表", description = "获取所有站点，可按名称关键字模糊筛选")
    @Parameter(name = "keyword", description = "站点名称关键字（模糊匹配）", required = false)
    public ResponseEntity<List<Stop>> getAll(@RequestParam(required = false) String keyword)
    {
        return ResponseEntity.ok(stopService.getStops(keyword));
    }
}
