package me.ningyu.app.locator.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
