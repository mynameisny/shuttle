package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.RouteDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.route.entity.QRoute;
import me.ningyu.app.locator.domain.route.entity.Route;
import me.ningyu.app.locator.service.RouteService;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody RouteDto dto)
    {
        Route route = routeService.update(id, dto);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        RouteDto routeDto = routeService.get(id);
        return ResponseEntity.ok(routeDto);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Route.class, bindings = RouteController.RouteBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Route> list = routeService.list(predicate, pageable);
        list.map(stop -> RouteDto.builder().name(stop.getName()).build());
        return ResponseEntity.ok(list);
    }

    private static class RouteBinding implements QuerydslBinderCustomizer<QRoute>
    {
        @Override
        public void customize(QuerydslBindings bindings, QRoute root)
        {
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
        }
    }
}
