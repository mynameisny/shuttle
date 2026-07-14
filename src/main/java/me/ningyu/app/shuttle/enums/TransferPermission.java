package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS transfers (fare_attributes.txt)
 * 允许换乘次数
 */
@Getter
public enum TransferPermission
{
    NO_TRANSFER(0, "不允许换乘"),
    ONE_TRANSFER(1, "允许1次换乘"),
    TWO_TRANSFERS(2, "允许2次换乘"),
    UNLIMITED(null, "不限换乘次数");

    private final Integer code;
    private final String name;

    TransferPermission(Integer code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
