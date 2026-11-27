package me.ningyu.app.shuttle.model.iam;

import java.util.List;

/**
 * 分配角色请求（全量替换用户角色）。
 */
public record AssignRolesRequest(List<String> roles)
{
}
