package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.enums.StationStatus;
import me.ningyu.app.locator.common.vo.AddressDto;
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


    @PostMapping
    @ApiOperation(value = "添加站点")
    public ResponseEntity<?> add(@RequestBody @Validated StationDto dto, UriComponentsBuilder builder)
    {
        Station saved = stationService.add(dto);
        URI location = builder.replacePath("/stations/{code}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{code}")
    @ApiOperation(value = "删除站点")
    public ResponseEntity<?> remove(@PathVariable String code)
    {
        stationService.remove(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改站点")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody StationDto dto)
    {
        Station station = stationService.update(id, dto);
        return ResponseEntity.ok(station);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查看站点")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        StationDto stationDto = stationService.get(id);
        return ResponseEntity.ok(stationDto);
    }

    @GetMapping
    @ApiOperation(value = "列出站点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "站点名称"),
            @ApiImplicitParam(name = "address", value = "站点地址"),
            @ApiImplicitParam(name = "latitude", value = "经度"),
            @ApiImplicitParam(name = "longitude", value = "纬度"),
            @ApiImplicitParam(name = "zipCode", value = "邮政编码")
    })
    public ResponseEntity<?> list(@QuerydslPredicate(root = Station.class, bindings = StationBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Station> list = stationService.list(predicate, pageable);
        Page<StationDto> page = list.map(stop -> StationDto.builder().name(stop.getName()).address(AddressDto.builder().name(stop.getAddress().getName()).build()).description(stop.getDescription()).build());
        return ResponseEntity.ok(page);
    }


    private static class StationBinding implements QuerydslBinderCustomizer<QStation>
    {
        @Override
        public void customize(QuerydslBindings bindings, QStation root)
        {
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.description).first(StringExpression::containsIgnoreCase);
        }
    }
}
