package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.facade.service.ShapeService;
import me.ningyu.app.shuttle.facade.service.TripService;
import me.ningyu.app.shuttle.model.schedule.AddStopTimeRequest;
import me.ningyu.app.shuttle.model.schedule.CreateTripRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateStopTimeRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateTripRequest;
import me.ningyu.app.shuttle.model.shape.SaveShapeRequest;
import me.ningyu.app.shuttle.model.shape.TripShapeView;
import me.ningyu.app.shuttle.model.stop.StopUsageView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "班次管理")
public class TripController
{
    private final TripService tripService;
    private final ShapeService shapeService;

    @PostMapping
    @Operation(summary = "创建班次", description = "创建一个新的班次（对应 GTFS trips.txt）")
    public ResponseEntity<Trip> create(@Valid @RequestBody CreateTripRequest request)
    {
        Trip trip = tripService.createTrip(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(trip.getId())
                .toUri();
        return ResponseEntity.created(location).body(trip);
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑班次", description = "更新已有班次")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Trip> update(@PathVariable Long id, @Valid @RequestBody UpdateTripRequest request)
    {
        Trip updated = tripService.updateTrip(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班次", description = "根据ID删除班次")
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

    @GetMapping
    @Operation(summary = "获取班次列表", description = "获取所有班次列表；支持按 GTFS trip_id 精确筛选（无匹配返回空列表）或按服务日历ID筛选")
    public ResponseEntity<List<Trip>> getAll(@RequestParam(required = false) String serviceId,
                                             @RequestParam(required = false) String tripId)
    {
        if (tripId != null && !tripId.isBlank())
        {
            return tripService.findTripByGtfsTripId(tripId)
                    .map(t -> ResponseEntity.ok(List.of(t)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }
        if (serviceId != null && !serviceId.isBlank())
        {
            List<Trip> trips = tripService.getTripsByServiceId(serviceId);
            return ResponseEntity.ok(trips);
        }
        List<Trip> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }

    // ---------- 班次的形状（1:1 单例子资源） ----------

    @GetMapping("/{id}/shape")
    @Operation(summary = "获取班次形状", description = "获取班次绑定的形状点列与沿途停站站点（管理端地图页数据源）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<TripShapeView> getShape(@PathVariable Long id)
    {
        return ResponseEntity.ok(shapeService.getTripShape(id));
    }

    @PutMapping("/{id}/shape")
    @Operation(summary = "保存班次形状", description = "全量替换班次形状点列（自动编号并按haversine累计里程），并绑定到班次；保存前校验形状必须经过全部停站（30米容差）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<TripShapeView> saveShape(@PathVariable Long id, @Valid @RequestBody SaveShapeRequest request)
    {
        return ResponseEntity.ok(shapeService.saveTripShape(id, request));
    }

    @DeleteMapping("/{id}/shape")
    @Operation(summary = "删除班次形状", description = "删除班次绑定的形状全部点列，并解除 trip.shape_id 绑定（幂等，管理端地图页清空操作）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> deleteShape(@PathVariable Long id)
    {
        shapeService.removeTripShape(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- 班次的停站时刻（子资源，定位：站序） ----------

    @PostMapping("/{id}/stop-times")
    @Operation(summary = "追加停站",
            description = "向班次停站序列末尾追加一个站点（管理端站点维护页「线路下加站」）。"
                    + "stop_sequence 按现有最大站序 +1 分配；到/发时间必填（HH:MM 或 HH:MM:SS）；"
                    + "同班次重复停靠同一站点返回 409")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<StopUsageView> addStopTime(@PathVariable Long id,
                                                     @Valid @RequestBody AddStopTimeRequest request)
    {
        return ResponseEntity.ok(tripService.addStopTime(id, request));
    }

    @PutMapping("/{id}/stop-times/order")
    @Operation(summary = "重排班次停站顺序",
            description = "按传入的有序站点主键列表重写 stop_sequence（1起连续递增）；"
                    + "到/发时刻随行整体移动。列表须与班次现有实体站点停站完全一致（400 兜底）；"
                    + "含 location 型停站的班次不支持重排")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> reorderStopTimes(@PathVariable Long id,
                                                 @RequestBody List<Long> orderedStopPks)
    {
        tripService.reorderStopTimes(id, orderedStopPks);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stop-times/{stopSequence}")
    @Operation(summary = "更新停站时刻",
            description = "更新班次中指定站序的到/发时刻与时间精度（站点维护页内联编辑）。"
                    + "字段均可选：null=不修改，空白串=清空；时间支持 HH:MM / HH:MM:SS（不支持 GTFS 跨日 24:00+）；"
                    + "timepoint 取值 APPROXIMATE（近似）/ EXACT（精确）；到站不得晚于发车")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "班次ID", schema = @Schema(type = "long")),
            @Parameter(name = "stopSequence", in = ParameterIn.PATH, required = true, description = "站序（stop_sequence）", schema = @Schema(type = "integer"))
    })
    public ResponseEntity<StopUsageView> updateStopTime(@PathVariable Long id,
                                                        @PathVariable Integer stopSequence,
                                                        @RequestBody UpdateStopTimeRequest request)
    {
        return ResponseEntity.ok(tripService.updateStopTime(id, stopSequence, request));
    }
}