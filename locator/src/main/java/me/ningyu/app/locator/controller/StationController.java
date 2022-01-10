package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.service.StationService;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "站点管理接口")
@RestController
@Slf4j
public class StationController
{
    private final StationService stationService;

    public StationController(StationService stationService)
    {
        this.stationService = stationService;
    }
}
