package me.ningyu.app.shuttle.notification;

import lombok.Getter;

/**
 * 发送策略（整体成功判定与渠道编排语义，与通知级别分离）。
 */
@Getter
public enum DeliveryPolicy
{
    /** 对所有指定渠道分别发送，互不阻塞。 */
    ALL("全渠道"),

    /** 任意一个渠道成功即可满足策略目标。 */
    ANY("任一成功"),

    /** 按顺序发送，失败后尝试下一个渠道。 */
    FALLBACK("顺序降级"),

    /** 主渠道发送失败时启用备用渠道。 */
    PRIMARY_WITH_FALLBACK("主渠道+备用");

    private final String name;

    DeliveryPolicy(String name)
    {
        this.name = name;
    }
}
