package me.ningyu.app.locator.controller;


import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.common.vo.UserDto;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.user.entity.QUser;
import me.ningyu.app.locator.domain.user.entity.User;
import me.ningyu.app.locator.service.UserService;
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

@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController
{
    private final UserService userService;


    public UserController(UserService userService)
    {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated UserDto dto, UriComponentsBuilder builder)
    {
        User saved = userService.add(dto);
        URI location = builder.replacePath("/users/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        userService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody UserDto dto)
    {
        User user = userService.update(id, dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        UserDto userDto = userService.get(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = User.class, bindings = UserController.UserBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<User> list = userService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }

    private static class UserBinding implements QuerydslBinderCustomizer<QUser>
    {
        @Override
        public void customize(QuerydslBindings bindings, QUser root)
        {
            bindings.bind(root.code).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.email).first(StringExpression::eq);
            bindings.bind(root.mobile).first(StringExpression::eq);
            bindings.bind(root.telephone).first(StringExpression::eq);
            bindings.bind(root.gender).first(EnumExpression::eq);
            bindings.bind(root.locked).first(BooleanExpression::eq);
        }
    }

}
