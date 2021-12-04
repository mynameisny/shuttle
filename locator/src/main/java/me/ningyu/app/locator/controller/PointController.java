package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.ningyu.app.locator.controller.binder.PointSearchBinding;
import me.ningyu.app.locator.entity.Point;
import me.ningyu.app.locator.service.PointService;
import me.ningyu.app.locator.vo.PointDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Api(tags = "坐标管理接口")
@RestController
@RequestMapping("/points")
public class PointController
{
    private final PointService pointService;

    public PointController(PointService pointService)
    {
        this.pointService = pointService;
    }


    @Operation(summary = "添加坐标点")
    @PostMapping
    public ResponseEntity<?> add(@RequestBody PointDto pointDto, UriComponentsBuilder builder)
    {
        PointDto saved = pointService.save(pointDto);
        URI location = builder.replacePath("/points/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @ApiOperation(value = "删除指定id的坐标", notes = "物理删除，请谨慎")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        pointService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PointDto pointDto)
    {
        Point entity = pointService.findById(id);
        if (entity == null)
        {
            throw new RuntimeException(String.format("找不到%d的坐标点", id));
        }

        BeanUtils.copyProperties(entity, pointDto);
        Point updated = pointService.update(entity);
        return ResponseEntity.ok().body(updated);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePart(@PathVariable Long id, @RequestBody PointDto pointDto)
    {
        Point entity = pointService.findById(id);
        if (entity == null)
        {
            throw new RuntimeException(String.format("找不到%d的坐标点", id));
        }

        BeanUtils.copyProperties(entity, pointDto);
        Point updated = pointService.update(entity);

        return ResponseEntity.ok(updated);
    }

    @ApiOperation(value = "查询满足条件的坐标点")
    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = PointDto.class, bindings = PointSearchBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Point> page = pointService.list(predicate, pageable);

        page.map(point ->
        {
            PointDto dto = new PointDto();
            BeanUtils.copyProperties(point, dto);
            return dto;
        });

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id)
    {
        PointDto point = new PointDto();
        Point entity = pointService.findById(id);
        BeanUtils.copyProperties(entity, point);

        return ResponseEntity.ok().body(point);
    }
}
