package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.DeviceDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.device.entity.Device;
import me.ningyu.app.locator.domain.device.entity.QDevice;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.DeviceService;
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

@Api(tags = "设备管理接口")
@RestController
@RequestMapping("/devices")
@Slf4j
public class DeviceController
{
    private final DeviceService deviceService;


    public DeviceController(DeviceService deviceService)
    {
        this.deviceService = deviceService;
    }


    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated DeviceDto dto, UriComponentsBuilder builder)
    {
        Device saved = deviceService.add(dto);
        URI location = builder.replacePath("/devices/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        deviceService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody DeviceDto dto)
    {
        Device device = deviceService.update(id, dto);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        DeviceDto deviceDto = deviceService.get(id);
        return ResponseEntity.ok(deviceDto);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Station.class, bindings = DeviceController.DeviceBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Device> list = deviceService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }

    private static class DeviceBinding implements QuerydslBinderCustomizer<QDevice>
    {
        @Override
        public void customize(QuerydslBindings bindings, QDevice root)
        {
            bindings.bind(root.vendor).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.model).first(StringExpression::containsIgnoreCase);
        }
    }
}
