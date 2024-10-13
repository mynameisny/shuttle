package me.ningyu.app.easymonger.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.dto.UserDto;
import me.ningyu.app.easymonger.model.vo.UserVo;
import me.ningyu.app.easymonger.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController
{
    private final UserService userService;
    
    
    /**
     * 新增用户
     * @param dto   新增用户参数
     * @return  用户对象
     */
    @PostMapping
    public ResponseEntity<UserVo> addUser(@RequestBody @Validated UserDto dto, @QuerydslPredicate(root = User.class) Predicate predicate)
    {
        UserVo vo = userService.add(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/users/{userCode}").buildAndExpand(vo.getCode()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<?> listUsers(@QuerydslPredicate(root = User.class) Predicate predicate, @PageableDefault(size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "modifiedDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        Page<UserVo> list = userService.list(predicate, pageable);
        return ResponseEntity.ok().body(list);
    }

    
}
