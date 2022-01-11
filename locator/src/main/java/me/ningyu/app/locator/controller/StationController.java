package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.PointDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;


@Api(tags = "站点管理接口")
@RestController
@RequestMapping(name = "/points")
@Slf4j
public class StationController
{
    private final StationService stationService;

    public StationController(StationService stationService)
    {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody StationDto dto, UriComponentsBuilder builder)
    {
        Station saved = stationService.add(dto);
        URI location = builder.replacePath("/points/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }
}
