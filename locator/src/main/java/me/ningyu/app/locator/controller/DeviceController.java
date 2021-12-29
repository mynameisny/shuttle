package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.service.DeviceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
