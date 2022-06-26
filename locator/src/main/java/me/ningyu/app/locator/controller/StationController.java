package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Api(tags = "站点管理")
@RestController
@RequestMapping("/stations")
@Slf4j
public class StationController
{
    @Setter(onMethod_ = {@Autowired})
    private StationService stationService;


    /**
     * 添加一个站点
     * @param dto
     * @param builder
     * @return
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated StationDto dto, UriComponentsBuilder builder)
    {
        Station saved = stationService.add(dto);
        URI location = builder.replacePath("/points/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /**
     * 删除站点
     * @param id 站点ID
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        stationService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody StationDto dto)
    {
        Station station = stationService.update(id, dto);
        return ResponseEntity.ok(station);
    }

    /**
     * 查看站点
     * @param id 站点ID
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        StationDto stationDto = stationService.get(id);
        return ResponseEntity.ok(stationDto);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Station.class, bindings = StationBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Station> list = stationService.list(predicate, pageable);
        list.map(stop -> StationDto.builder().name(stop.getName()).address(stop.getAddress()).description(stop.getDescription()).latitude(stop.getLatitude()).longitude(stop.getLongitude()).build());
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
