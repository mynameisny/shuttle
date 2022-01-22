package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.StationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import sun.tools.tree.LongExpression;

import java.net.URI;
import java.util.List;


@Api(tags = "站点管理接口")
@RestController
@RequestMapping(name = "/points")
@Slf4j
public class StationController
{
    private final StationService stationService;

    public StationController(StationService stationService)
    {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody StationDto dto, UriComponentsBuilder builder)
    {
        Station saved = stationService.add(dto);
        URI location = builder.replacePath("/points/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        stationService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody StationDto dto)
    {
        Station station = stationService.update(id, dto);
        return ResponseEntity.ok(station);
    }

    @RequestMapping ("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        StationDto stationDto = stationService.get(id);
        return ResponseEntity.ok(stationDto);
    }

    @RequestMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Station.class, bindings = StationBinding.class) Predicate predicate, Pageable pageable)
    {
        List<StationDto> list = stationService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }

    private static class StationBinding implements QuerydslBinderCustomizer<QStation>
    {
        @Override
        public void customize(QuerydslBindings bindings, QStation root)
        {
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.address).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.latitude).first(NumberExpression::eq);
            bindings.bind(root.longitude).first(NumberExpression::eq);
        }
    }
}
