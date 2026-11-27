package me.ningyu.app.shuttle.iam.domain;

/**
 * 账号状态。
 *
 * <p>{@code ACTIVE} 启用；{@code DISABLED} 禁用（保留数据，禁止登录）；
 * {@code LOCKED} 锁定（如连续失败多次，需管理员解锁）。
 */
public enum AccountStatus
{
    ACTIVE,
    DISABLED,
    LOCKED
}
