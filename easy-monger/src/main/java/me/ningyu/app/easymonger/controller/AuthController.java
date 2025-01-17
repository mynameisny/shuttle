package me.ningyu.app.easymonger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.model.vo.UserVo;
import me.ningyu.app.easymonger.service.UserService;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController
{
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserVo> register(@RequestBody @Validated RegistrationDto dto)
    {
        UserDto userDto = new UserDto();
        userDto.setCode(dto.getUsername());
        userDto.setPassword(dto.getPassword());
        userDto.setMobile(dto.getMobile());

        UserVo vo = userService.add(userDto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/users/{userId}").buildAndExpand(vo.getId()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }
}
