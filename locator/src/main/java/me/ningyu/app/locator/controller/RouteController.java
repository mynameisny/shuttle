package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.RouteDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.route.entity.Route;
import me.ningyu.app.locator.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Api(tags = "路线管理接口")
@RestController
@RequestMapping("/routes")
@Slf4j
public class RouteController
{
    private final RouteService routeService;

    public RouteController(RouteService routeService)
    {
        this.routeService = routeService;
    }


    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated RouteDto dto, UriComponentsBuilder builder)
    {
        Route saved = routeService.add(dto);
        URI location = builder.replacePath("/routes/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        routeService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
