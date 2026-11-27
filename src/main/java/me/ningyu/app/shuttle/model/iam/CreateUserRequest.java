package me.ningyu.app.shuttle.model.iam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建用户请求。
 */
public record CreateUserRequest(
        @NotBlank(message = "用户名不能为空") String username,
        @NotBlank(message = "密码不能为空") @Size(min = 6, message = "密码至少 6 位") String password,
        String displayName,
        String phone,
        String email)
{
}
