package me.ningyu.app.shuttle.model.iam;

import java.util.List;

/**
 * 登录响应：JWT 凭证 + 主体信息 + 角色。
 */
public record LoginResponse(
        String token,
        String tokenType,
        String username,
        String displayName,
        List<String> roles)
{
}
