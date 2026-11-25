package me.ningyu.app.shuttle.notification;

import lombok.Getter;

/**
 * 通知渠道。
 *
 * <p>本期仅 INBOX（站内信）实际落地，其余渠道在 Delivery Center 阶段实现发送。
 */
@Getter
public enum ChannelType
{
    EMAIL("邮件"),
    SMS("短信"),
    FEISHU("飞书"),
    WECHAT("微信"),
    INBOX("站内信"),
    WEBHOOK("Webhook");

    private final String name;

    ChannelType(String name)
    {
        this.name = name;
    }
}
