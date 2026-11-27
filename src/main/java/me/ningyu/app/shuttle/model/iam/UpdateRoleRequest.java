package me.ningyu.app.shuttle.model.iam;

import jakarta.validation.constraints.NotBlank;

/**
 * 更新角色请求（编码不可改）。
 */
public record UpdateRoleRequest(
        @NotBlank(message = "角色名称不能为空") String name,
        String description)
{
}
