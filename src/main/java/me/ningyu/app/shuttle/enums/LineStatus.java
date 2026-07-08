package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 线路状态
 */
@Getter
public enum LineStatus
{
    ACTIVE("启用"),
    INACTIVE("停用");

    private final String name;

    LineStatus(String name)
    {
        this.name = name;
    }
}
