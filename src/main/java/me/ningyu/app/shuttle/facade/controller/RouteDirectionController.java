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
import me.ningyu.app.shuttle.domain.entity.RouteDirection;
import me.ningyu.app.shuttle.facade.service.RouteDirectionService;
import me.ningyu.app.shuttle.model.route.CreateRouteDirectionRequest;
import me.ningyu.app.shuttle.model.route.UpdateRouteDirectionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "带方向的线路管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/route-directions")
@Slf4j
@RequiredArgsConstructor
public class RouteDirectionController
{
    private final RouteDirectionService routeDirectionService;

    @PostMapping
    @Operation(summary = "创建带方向的线路", description = "创建新的带方向线路（需关联线路模板）")
    public ResponseEntity<RouteDirection> create(@Valid @RequestBody CreateRouteDirectionRequest request)
    {
        RouteDirection routeDirection = routeDirectionService.createRouteDirection(request);
        return ResponseEntity.ok(routeDirection);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新带方向的线路", description = "更新指定带方向线路的信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "带方向线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<RouteDirection> update(@PathVariable Long id, @Valid @RequestBody UpdateRouteDirectionRequest request)
    {
        RouteDirection routeDirection = routeDirectionService.updateRouteDirection(id, request);
        return ResponseEntity.ok(routeDirection);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除带方向的线路", description = "删除指定带方向的线路（需确保无关联站点和排班）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "带方向线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        routeDirectionService.deleteRouteDirection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取带方向线路详情", description = "根据ID获取带方向线路的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "带方向线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<RouteDirection> getById(@PathVariable Long id)
    {
        RouteDirection routeDirection = routeDirectionService.getRouteDirectionById(id);
        return ResponseEntity.ok(routeDirection);
    }

    @GetMapping
    @Operation(summary = "获取所有带方向的线路列表", description = "获取所有带方向线路的列表")
    public ResponseEntity<List<RouteDirection>> getAll()
    {
        List<RouteDirection> routeDirections = routeDirectionService.getAllRouteDirections();
        return ResponseEntity.ok(routeDirections);
    }

    @GetMapping("/by-route/{routeId}")
    @Operation(summary = "根据线路ID获取带方向的线路列表", description = "获取指定线路下所有带方向的线路")
    @Parameters({
            @Parameter(name = "routeId", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<List<RouteDirection>> getByRouteId(@PathVariable Long routeId)
    {
        List<RouteDirection> routeDirections = routeDirectionService.getRouteDirectionsByRouteId(routeId);
        return ResponseEntity.ok(routeDirections);
    }
}
