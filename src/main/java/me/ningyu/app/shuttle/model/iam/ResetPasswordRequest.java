package me.ningyu.app.shuttle.model.iam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 重置密码请求。
 */
public record ResetPasswordRequest(
        @NotBlank(message = "密码不能为空") @Size(min = 6, message = "密码至少 6 位") String password)
{
}
