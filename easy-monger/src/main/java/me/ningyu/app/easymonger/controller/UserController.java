package me.ningyu.app.easymonger.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.dto.UserAddDto;
import me.ningyu.app.easymonger.model.dto.UserRegisterDto;
import me.ningyu.app.easymonger.model.dto.UserUpdateDto;
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
     * 用户注册
     * @param dto   用户注册参数
     * @return  用户对象 {@code UserVo}
     */
    @PostMapping("/register")
    public ResponseEntity<UserVo> registerUser(@RequestBody @Validated UserRegisterDto dto)
    {
        UserVo vo = userService.register(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/users/{userCode}").buildAndExpand(vo.getCode()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.ACCEPTED);
    }

    /**
     * 新增用户
     * @param dto   新增用户参数
     * @return  用户对象 {@code UserVo}
     */
    @PostMapping
    public ResponseEntity<UserVo> addUser(@RequestBody @Validated UserAddDto dto)
    {
        UserVo vo = userService.add(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/users/{userCode}").buildAndExpand(vo.getCode()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }
    
    /**
     * 删除用户
     * @param code 用户编码
     * @return HTTP 204
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteUser(@PathVariable("code") String code, @RequestParam(required = false, defaultValue = "false") boolean force)
    {
        userService.delete(code, force);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 修改用户
     * @param code  用户编码
     * @param dto   修改用户参数
     * @return 修改后的用户对象 {@code UserVo}
     */
    @PutMapping("/{code}")
    public ResponseEntity<UserVo> updateUser(@PathVariable("code") String code, @RequestBody @Validated UserUpdateDto dto)
    {
        return ResponseEntity.ok(userService.update(code, dto));
    }
    
    /**
     * 列出用户
     * @param predicate 查询条件
     * @param pageable  分页参数
     * @return  分页后的用户
     */
    @GetMapping
    public ResponseEntity<Page<UserVo>> listUsers(@QuerydslPredicate(root = User.class) Predicate predicate, @PageableDefault(size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "modifiedDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        Page<UserVo> list = userService.list(predicate, pageable);
        return ResponseEntity.ok().body(list);
    }
    
    /**
     * 查看用户
     * @param code  用户编码
     * @return 用户对象 {@code UserVo}
     */
    @GetMapping("/{code}")
    public ResponseEntity<?> getUser(@PathVariable(name = "code") String code)
    {
        return ResponseEntity.ok(userService.get(code));
    }

    
}
