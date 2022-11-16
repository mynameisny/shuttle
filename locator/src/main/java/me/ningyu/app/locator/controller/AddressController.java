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
import me.ningyu.app.locator.common.vo.AddressDto;
import me.ningyu.app.locator.domain.map.entity.Address;
import me.ningyu.app.locator.domain.map.entity.QAddress;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.AddressService;
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


@Api(tags = "地址管理")
@RestController
@RequestMapping("/addresses")
@Slf4j
public class AddressController
{
    @Setter(onMethod_ = {@Autowired})
    private AddressService addressService;


    @PostMapping
    @ApiOperation(value = "添加地址")
    public ResponseEntity<?> add(@RequestBody @Validated AddressDto dto, UriComponentsBuilder builder)
    {
        AddressDto address = addressService.add(dto);
        URI location = builder.replacePath("/stations/{code}").buildAndExpand(address.getCode()).toUri();
        return ResponseEntity.created(location).body(address);
    }

    @DeleteMapping("/{code}")
    @ApiOperation(value = "删除地址")
    public ResponseEntity<?> remove(@PathVariable String code)
    {
        addressService.remove(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{code}")
    @ApiOperation(value = "修改地址")
    public ResponseEntity<?> update(@PathVariable String code, @Validated @RequestBody AddressDto dto)
    {
        AddressDto address = addressService.update(code, dto);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/{code}")
    @ApiOperation(value = "查看地址")
    public ResponseEntity<?> get(@PathVariable String code)
    {
        AddressDto address = addressService.get(code);
        return ResponseEntity.ok(address);
    }

    @GetMapping
    @ApiOperation(value = "列出地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "地址编码"),
            @ApiImplicitParam(name = "name", value = "地址名称"),
            @ApiImplicitParam(name = "coordinate", value = "坐标系"),
            @ApiImplicitParam(name = "latitude", value = "经度"),
            @ApiImplicitParam(name = "longitude", value = "纬度"),
            @ApiImplicitParam(name = "areaCode", value = "地区编码"),
            @ApiImplicitParam(name = "description", value = "地址描述"),
    })
    public ResponseEntity<?> list(@QuerydslPredicate(root = Address.class, bindings = AddressBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<AddressDto> list = addressService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }


    private static class AddressBinding implements QuerydslBinderCustomizer<QAddress>
    {
        @Override
        public void customize(QuerydslBindings bindings, QAddress root)
        {
            bindings.bind(root.code).first(StringExpression::eq);
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.coordinate).first(EnumExpression::eq);
            bindings.bind(root.latitude).first(StringExpression::eq);
            bindings.bind(root.longitude).first(StringExpression::eq);
            bindings.bind(root.areaCode).first(StringExpression::contains);
            bindings.bind(root.description).first(StringExpression::containsIgnoreCase);
        }
    }
}
