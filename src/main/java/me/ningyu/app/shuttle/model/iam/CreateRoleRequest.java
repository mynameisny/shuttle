package me.ningyu.app.shuttle.model.iam;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建角色请求。
 */
public record CreateRoleRequest(
        @NotBlank(message = "角色编码不能为空") String code,
        @NotBlank(message = "角色名称不能为空") String name,
        String description)
{
}
