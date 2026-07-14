package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS transfer_type
 * 对应 transfers.txt 中的 transfer_type 字段
 */
@Getter
public enum TransferType
{
    RECOMMENDED(0, "推荐换乘点"),
    TIMED(1, "定时换乘"),
    MINIMUM_TIME(2, "最小换乘时间"),
    NOT_POSSIBLE(3, "不可换乘");

    private final int code;
    private final String name;

    TransferType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
