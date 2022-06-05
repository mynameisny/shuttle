package me.ningyu.app.locator.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.enums.RouteStatus;
import me.ningyu.app.locator.common.vo.RouteDto;
import me.ningyu.app.locator.domain.route.entity.QRoute;
import me.ningyu.app.locator.domain.route.entity.Route;
import me.ningyu.app.locator.service.RouteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;

@Api(tags = "路线管理接口")
@RestController
@RequestMapping(value = "/routes")
@Slf4j
public class RouteController
{
    @Setter(onMethod_ = {@Autowired})
    private RouteService routeService;


    /**
     * 添加一条线路
     * @param dto 需要创建的线路
     * @param builder 工具类UriComponents，用于根据URL和参数来构建路径，详见：https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-uri-building
     * @return 成功后返回HTTP 201状态码
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated RouteDto dto, UriComponentsBuilder builder)
    {
        Route saved = routeService.add(dto);
        URI location = builder.replacePath("/routes/{id}").buildAndExpand(saved.getId()).toUri();

        RouteDto result = new RouteDto();
        BeanUtils.copyProperties(saved, result);

        return ResponseEntity.created(location).body(result);
    }

    /**
     * 删除指定ID的线路
     * @param id 线路ID
     * @return 成功后返回HTTP 204响应码
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        RouteDto origin = routeService.get(id);
        log.info("要删除的线路为{}", origin);

        routeService.remove(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 修改指定ID的线路
     * @param id 线路ID
     * @param dto 要修改成的线路
     * @return 线路对象
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody RouteDto dto)
    {
        RouteDto origin = routeService.get(id);
        log.info("修改前的线路为{}", origin);

        Route updated = routeService.update(id, dto);
    
        RouteDto result = new RouteDto();
        BeanUtils.copyProperties(updated, result);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 查找指定ID的线路
     * @param id 线路ID
     * @return 线路对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        RouteDto routeDto = routeService.get(id);
        return ResponseEntity.ok(routeDto);
    }

    /**
     * 列出满足条件的线路
     * @param predicate 查询条件
     * @param pageable 分页参数
     * @return 包含分页信息的线路列表
     */
    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Route.class, bindings = RouteController.RouteBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Route> list = routeService.list(predicate, pageable);
        list.map(route -> RouteDto.builder().code(route.getCode()).name(route.getName()).colorHex(route.getColorHex()).status(route.getStatus()).build());
        return ResponseEntity.ok(list);
    }

    private static class RouteBinding implements QuerydslBinderCustomizer<QRoute>
    {
        @Override
        public void customize(QuerydslBindings bindings, QRoute root)
        {
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.code).first(StringExpression::eq);
            bindings.bind(root.colorHex).first(StringExpression::eq);
            bindings.bind(root.description).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.origin).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.terminal).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.status).all((path, value) -> Optional.empty());
        }
    }
}
