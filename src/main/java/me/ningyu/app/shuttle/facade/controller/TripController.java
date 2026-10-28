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
import me.ningyu.app.shuttle.facade.service.TripService;
import me.ningyu.app.shuttle.model.schedule.CreateTripRequest;
import me.ningyu.app.shuttle.model.schedule.UpdateTripRequest;
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

    @GetMapping("/by-trip-id/{tripId}")
    @Operation(summary = "根据tripId获取班次", description = "根据GTFS trip_id获取班次信息")
    @Parameters({
            @Parameter(name = "tripId", in = ParameterIn.PATH, required = true, description = "班次唯一标识（trip_id）", schema = @Schema(type = "string"))
    })
    public ResponseEntity<Trip> getByTripId(@PathVariable String tripId)
    {
        Trip trip = tripService.getTripByTripId(tripId);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/by-route/{routeId}")
    @Operation(summary = "根据线路查询班次", description = "获取指定线路下的所有班次")
    @Parameters({
            @Parameter(name = "routeId", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<List<Trip>> getByRoute(@PathVariable Long routeId)
    {
        List<Trip> trips = tripService.getTripsByRouteId(routeId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping
    @Operation(summary = "获取班次列表", description = "获取所有班次列表，可按服务日历ID筛选")
    public ResponseEntity<List<Trip>> getAll(@RequestParam(required = false) String serviceId)
    {
        if (serviceId != null && !serviceId.isBlank())
        {
            List<Trip> trips = tripService.getTripsByServiceId(serviceId);
            return ResponseEntity.ok(trips);
        }
        List<Trip> trips = tripService.getAllTrips();
        return ResponseEntity.ok(trips);
    }
}