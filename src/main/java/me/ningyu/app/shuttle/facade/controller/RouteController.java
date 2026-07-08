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
import me.ningyu.app.shuttle.domain.entity.Route;
import me.ningyu.app.shuttle.facade.service.RouteService;
import me.ningyu.app.shuttle.model.route.CreateRouteRequest;
import me.ningyu.app.shuttle.model.route.UpdateRouteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "线路方向管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/routes")
@Slf4j
@RequiredArgsConstructor
public class RouteController
{
    private final RouteService routeService;

    @PostMapping
    @Operation(summary = "创建线路方向", description = "创建新的线路方向（需关联线路）")
    public ResponseEntity<Route> create(@Valid @RequestBody CreateRouteRequest request)
    {
        Route route = routeService.createRoute(request);
        return ResponseEntity.ok(route);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新线路方向", description = "更新指定线路方向的信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路方向ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Route> update(@PathVariable Long id, @Valid @RequestBody UpdateRouteRequest request)
    {
        Route route = routeService.updateRoute(id, request);
        return ResponseEntity.ok(route);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除线路方向", description = "删除指定线路方向（需确保无关联站点和班次）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路方向ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取线路方向详情", description = "根据ID获取线路方向的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路方向ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Route> getById(@PathVariable Long id)
    {
        Route route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @GetMapping
    @Operation(summary = "获取所有线路方向列表", description = "获取所有线路方向的列表")
    public ResponseEntity<List<Route>> getAll()
    {
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/by-line/{lineId}")
    @Operation(summary = "根据线路ID获取线路方向列表", description = "获取指定线路下所有线路方向")
    @Parameters({
            @Parameter(name = "lineId", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<List<Route>> getByLineId(@PathVariable Long lineId)
    {
        List<Route> routes = routeService.getRoutesByLineId(lineId);
        return ResponseEntity.ok(routes);
    }
}
