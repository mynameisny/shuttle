package me.ningyu.app.shuttle.notification;

import lombok.Getter;

/**
 * 通知级别（业务紧急程度）。
 *
 * <p>仅表示业务紧急程度，不自动决定渠道；渠道由规则独立配置。
 */
@Getter
public enum Severity
{
    CRITICAL("紧急"),
    HIGH("高"),
    NORMAL("普通"),
    LOW("低");

    private final String name;

    Severity(String name)
    {
        this.name = name;
    }
}
