package me.ningyu.app.shuttle.model.iam;

import me.ningyu.app.shuttle.iam.domain.AccountStatus;

/**
 * 更新用户请求（不含用户名与密码）。
 */
public record UpdateUserRequest(
        String displayName,
        String phone,
        String email,
        AccountStatus status)
{
}
