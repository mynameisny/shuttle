package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.PointDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        stationService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody StationDto dto)
    {
        Station station = stationService.update(id, dto);
        return ResponseEntity.ok(station);
    }

    @RequestMapping ("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        StationDto stationDto = stationService.get(id);
        return ResponseEntity.ok(stationDto);
    }

    @RequestMapping
    public ResponseEntity<?> list()
    {
        List<StationDto> list = stationService.list();
        return ResponseEntity.ok(list);
    }
}
