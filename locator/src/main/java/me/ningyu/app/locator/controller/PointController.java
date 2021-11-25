package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import me.ningyu.app.locator.controller.binder.PointSearchBinding;
import me.ningyu.app.locator.entity.Point;
import me.ningyu.app.locator.service.PointService;
import me.ningyu.app.locator.vo.PointDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/points")
public class PointController
{
    private final PointService pointService;

    public PointController(PointService pointService)
    {
        this.pointService = pointService;
    }


    @PostMapping
    public ResponseEntity<?> add(@RequestBody PointDto pointDto, UriComponentsBuilder builder)
    {
        Point saved = pointService.save(pointDto);
        URI location = builder.replacePath("/points/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id)
    {
        pointService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody PointDto pointDto)
    {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePart(@PathVariable String id, @RequestBody PointDto pointDto)
    {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = PointDto.class, bindings = PointSearchBinding.class) Predicate predicate, Pageable pageable)
    {
        List<Point> points = pointService.list(predicate, pageable);
        List<PointDto> result = new ArrayList<>();
        for (Point point : points)
        {
            PointDto dto = new PointDto();
            BeanUtils.copyProperties(point, dto);
            result.add(dto);
        }
        return ResponseEntity.ok(result);
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
