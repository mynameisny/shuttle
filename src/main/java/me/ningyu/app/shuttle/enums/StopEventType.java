package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 站点到发事件类型（业务扩展枚举，非 GTFS 规范）。
 *
 * <p>一次上报只对应「到站」或「发车」其一；同一条站点记录的两个实际时刻
 * 由两次上报分别填充（到站填实际到达，发车填实际发车）。
 */
@Getter
public enum StopEventType
{
    ARRIVAL("到站"),
    DEPARTURE("发车");

    private final String name;

    StopEventType(String name)
    {
        this.name = name;
    }
}
