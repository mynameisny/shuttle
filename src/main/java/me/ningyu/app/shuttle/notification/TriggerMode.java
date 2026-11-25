package me.ningyu.app.shuttle.notification;

import lombok.Getter;

/**
 * 通知触发模式（属于规则属性，不固化在事件类型上）。
 */
@Getter
public enum TriggerMode
{
    /** 事件发生后自动匹配、生成通知并分发。 */
    AUTO("自动"),

    /** 事件发生后生成待人工确认事项，管理员确认后再发送。 */
    MANUAL("手动");

    private final String name;

    TriggerMode(String name)
    {
        this.name = name;
    }
}
