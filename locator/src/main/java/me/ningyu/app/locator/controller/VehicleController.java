package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.service.VehicleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "载具管理接口")
@RestController
@RequestMapping("/vehicles")
@Slf4j
public class VehicleController
{
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService)
    {
        this.vehicleService = vehicleService;
    }
}
