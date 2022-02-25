package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.DeviceDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.device.entity.Device;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.DeviceService;
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
}
