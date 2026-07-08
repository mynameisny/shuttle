package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 司机状态
 */
@Getter
public enum DriverStatus
{
    ACTIVE("在岗"),
    INACTIVE("停用");

    private final String name;

    DriverStatus(String name)
    {
        this.name = name;
    }
}
