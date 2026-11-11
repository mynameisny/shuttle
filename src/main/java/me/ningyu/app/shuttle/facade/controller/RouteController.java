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
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.facade.service.RouteService;
import me.ningyu.app.shuttle.facade.service.TripService;
import me.ningyu.app.shuttle.model.route.CreateRouteRequest;
import me.ningyu.app.shuttle.model.route.RouteStopsView;
import me.ningyu.app.shuttle.model.route.UpdateRouteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "线路管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/routes")
@Slf4j
@RequiredArgsConstructor
public class RouteController
{
    private final RouteService routeService;
    private final TripService tripService;

    @PostMapping
    @Operation(summary = "创建线路", description = "创建新的线路（对应GTFS routes.txt）")
    public ResponseEntity<Route> create(@Valid @RequestBody CreateRouteRequest request)
    {
        Route route = routeService.createRoute(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(route.getId())
                .toUri();
        return ResponseEntity.created(location).body(route);
    }

    @PutMapping("/sort")
    @Operation(summary = "批量排序线路", description = "按传入的有序 id 列表重写 route_sort_order（值为 1..N）")
    public ResponseEntity<Void> reorder(@RequestBody List<Long> orderedIds)
    {
        routeService.reorderRoutes(orderedIds);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新线路", description = "更新指定线路的信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Route> update(@PathVariable Long id, @Valid @RequestBody UpdateRouteRequest request)
    {
        Route route = routeService.updateRoute(id, request);
        return ResponseEntity.ok(route);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除线路", description = "删除指定线路（需确保无关联站点和班次）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取线路详情", description = "根据ID获取线路的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Route> getById(@PathVariable Long id)
    {
        Route route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{id}/stops")
    @Operation(summary = "获取线路站点", description = "正向查询：按方向分组返回该线路全部班次涉及的站点（去重并集、按站序排列），同名去程/返程站台靠方向上下文消歧")
    public ResponseEntity<RouteStopsView> getStops(
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
            @PathVariable Long id)
    {
        return ResponseEntity.ok(routeService.getRouteStops(id));
    }

    @GetMapping("/{id}/trips")
    @Operation(summary = "获取线路班次", description = "获取指定线路下的全部班次（嵌套子资源）")
    public ResponseEntity<List<Trip>> getTrips(
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
            @PathVariable Long id)
    {
        return ResponseEntity.ok(tripService.getTripsByRouteId(id));
    }

    @GetMapping
    @Operation(summary = "获取线路列表", description = "获取所有线路列表，可按所属机构筛选；withTags=true 时附带网络标签（route_networks 多标签）")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Long agencyId,
            @RequestParam(required = false, defaultValue = "false") boolean withTags)
    {
        if (withTags)
        {
            return ResponseEntity.ok(routeService.getAllRoutesWithTags(agencyId));
        }
        if (agencyId != null)
        {
            List<Route> routes = routeService.getRoutesByAgencyId(agencyId);
            return ResponseEntity.ok(routes);
        }
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }
}