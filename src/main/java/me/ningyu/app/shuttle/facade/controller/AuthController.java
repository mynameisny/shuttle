package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.application.AuthService;
import me.ningyu.app.shuttle.model.iam.LoginRequest;
import me.ningyu.app.shuttle.model.iam.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "登录", description = "用户名密码登录，成功返回 JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }
}
