package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
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
        StationDto station = stationService.add(dto);
        URI location = builder.replacePath("/stations/{code}").buildAndExpand(station.getCode()).toUri();
        return ResponseEntity.created(location).body(station);
    }

    @DeleteMapping("/{code}")
    @ApiOperation(value = "删除站点")
    public ResponseEntity<?> remove(@PathVariable String code)
    {
        stationService.remove(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    @ApiOperation(value = "修改站点")
    public ResponseEntity<?> update(@PathVariable String code, @Validated @RequestBody StationDto dto)
    {
        StationDto station = stationService.update(code, dto);
        return ResponseEntity.ok(station);
    }

    @GetMapping
    @ApiOperation(value = "列出站点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "站点编码"),
            @ApiImplicitParam(name = "name", value = "站点名称"),
            @ApiImplicitParam(name = "description", value = "站点描述"),
            @ApiImplicitParam(name = "status", value = "站点状态")
    })
    public ResponseEntity<?> list(@QuerydslPredicate(root = Station.class, bindings = StationBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<StationDto> list = stationService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{code}")
    @ApiOperation(value = "查看站点")
    public ResponseEntity<?> get(@PathVariable String code)
    {
        StationDto stationDto = stationService.get(code);
        return ResponseEntity.ok(stationDto);
    }


    private static class StationBinding implements QuerydslBinderCustomizer<QStation>
    {
        @Override
        public void customize(QuerydslBindings bindings, QStation root)
        {
            bindings.bind(root.code).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.description).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.status).first(EnumExpression::eq);
        }
    }
}
