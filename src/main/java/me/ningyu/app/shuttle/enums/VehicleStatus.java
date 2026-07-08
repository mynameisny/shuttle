package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * 车辆状态
 */
@Getter
public enum VehicleStatus
{
    ACTIVE("运营中"),
    MAINTENANCE("维修中"),
    INACTIVE("停用");

    private final String name;

    VehicleStatus(String name)
    {
        this.name = name;
    }
}
