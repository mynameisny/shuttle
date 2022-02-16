package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.BrandDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import me.ningyu.app.locator.service.BrandService;
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
}
