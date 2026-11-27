package me.ningyu.app.shuttle.model.iam;

import jakarta.validation.constraints.NotBlank;

/**
 * 授予/撤销权限请求（资源为 jCasbin 正则模式，动作为正则或 HTTP 方法）。
 */
public record GrantPermissionRequest(
        @NotBlank(message = "资源不能为空") String resource,
        @NotBlank(message = "动作不能为空") String action)
{
}
