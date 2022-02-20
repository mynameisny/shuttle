package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.BrandDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import me.ningyu.app.locator.domain.vehicle.entity.QBrand;
import me.ningyu.app.locator.service.BrandService;
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

@Api(tags = "品牌管理接口")
@RestController
@RequestMapping("/brands")
@Slf4j
public class BrandController
{
    private final BrandService brandService;


    public BrandController(BrandService brandService)
    {
        this.brandService = brandService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated BrandDto dto, UriComponentsBuilder builder)
    {
        Brand saved = brandService.add(dto);
        URI location = builder.replacePath("/brands/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        brandService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody BrandDto dto)
    {
        Brand brand = brandService.update(id, dto);
        return ResponseEntity.ok(brand);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        BrandDto brandDto = brandService.get(id);
        return ResponseEntity.ok(brandDto);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Brand.class, bindings = BrandController.BrandBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Brand> list = brandService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }

    private static class BrandBinding implements QuerydslBinderCustomizer<QBrand>
    {
        @Override
        public void customize(QuerydslBindings bindings, QBrand root)
        {
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.brief).first(StringExpression::containsIgnoreCase);
        }
    }
}
