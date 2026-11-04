package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.gtfs.Shape;
import me.ningyu.app.shuttle.facade.service.ShapeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 形状集合本身的查询（按 shape_id 定位点列）。
 * 班次维度的形状读写见 TripController 的 /api/trips/{id}/shape 单例子资源。
 */
@Tag(name = "形状管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/shapes")
@Slf4j
@RequiredArgsConstructor
public class ShapeController
{
    private final ShapeService shapeService;

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
