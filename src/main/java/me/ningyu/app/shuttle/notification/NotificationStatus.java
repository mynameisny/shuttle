package me.ningyu.app.shuttle.notification;

import lombok.Getter;

/**
 * 通知状态。
 */
@Getter
public enum NotificationStatus
{
    /** 待人工确认（手动规则生成）。 */
    PENDING_CONFIRMATION("待人工确认"),

    /** 待发送（自动规则已生成通知，但渠道发送留待 Delivery 阶段）。 */
    PENDING_DELIVERY("待发送"),

    /** 已完成（站内信等渠道已落地）。 */
    COMPLETED("已完成"),

    /** 失败。 */
    FAILED("失败"),

    /** 已跳过（幂等去重或条件未命中）。 */
    SKIPPED("已跳过");

    private final String name;

    NotificationStatus(String name)
    {
        this.name = name;
    }
}
