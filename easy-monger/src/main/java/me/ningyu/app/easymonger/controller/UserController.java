package me.ningyu.app.easymonger.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.dto.UserDto;
import me.ningyu.app.easymonger.model.vo.UserVo;
import me.ningyu.app.easymonger.service.UserService;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<UserVo> add(@RequestBody @Validated UserDto dto, @QuerydslPredicate(root = User.class) Predicate predicate)
    {
        UserVo vo = userService.add(dto, predicate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/users/{userCode}").buildAndExpand(vo.getCode()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }

    
}
