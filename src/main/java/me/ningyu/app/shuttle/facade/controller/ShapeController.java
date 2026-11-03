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
import me.ningyu.app.shuttle.domain.entity.gtfs.Shape;
import me.ningyu.app.shuttle.facade.service.ShapeService;
import me.ningyu.app.shuttle.model.shape.SaveShapeRequest;
import me.ningyu.app.shuttle.model.shape.TripShapeView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "形状管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/shapes")
@Slf4j
@RequiredArgsConstructor
public class ShapeController
{
    private final ShapeService shapeService;

    @GetMapping("/by-trip/{tripPk}")
    @Operation(summary = "按班次查询形状", description = "获取班次绑定的形状点列与沿途停站站点（管理端地图页数据源）")
    @Parameters({
            @Parameter(name = "tripPk", in = ParameterIn.PATH, required = true, description = "班次主键ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<TripShapeView> getByTrip(@PathVariable Long tripPk)
    {
        return ResponseEntity.ok(shapeService.getTripShape(tripPk));
    }

    @PutMapping("/by-trip/{tripPk}")
    @Operation(summary = "保存班次形状", description = "全量替换班次形状点列（自动编号并按haversine累计里程），并绑定到班次")
    @Parameters({
            @Parameter(name = "tripPk", in = ParameterIn.PATH, required = true, description = "班次主键ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<TripShapeView> saveByTrip(@PathVariable Long tripPk, @Valid @RequestBody SaveShapeRequest request)
    {
        return ResponseEntity.ok(shapeService.saveTripShape(tripPk, request));
    }

    @DeleteMapping("/by-trip/{tripPk}")
    @Operation(summary = "删除班次形状", description = "删除班次绑定的形状全部点列，并解除 trip.shape_id 绑定（幂等，管理端地图页清空操作）")
    @Parameters({
            @Parameter(name = "tripPk", in = ParameterIn.PATH, required = true, description = "班次主键ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> deleteByTrip(@PathVariable Long tripPk)
    {
        shapeService.removeTripShape(tripPk);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shapeId}")
    @Operation(summary = "按形状ID查询点列", description = "获取指定形状的全部坐标点（按顺序）")
    @Parameters({
            @Parameter(name = "shapeId", in = ParameterIn.PATH, required = true, description = "形状标识", schema = @Schema(type = "string"))
    })
    public ResponseEntity<List<Shape>> getByShapeId(@PathVariable String shapeId)
    {
        List<Shape> points = shapeService.getShapePoints(shapeId);
        if (points.isEmpty())
        {
            throw new IllegalArgumentException("形状不存在: " + shapeId);
        }
        return ResponseEntity.ok(points);
    }
}
