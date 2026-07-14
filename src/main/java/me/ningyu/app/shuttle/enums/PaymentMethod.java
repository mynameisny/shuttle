package me.ningyu.app.shuttle.enums;

import lombok.Getter;

/**
 * GTFS payment_method
 * 对应 fare_attributes.txt 中的 payment_method 字段
 */
@Getter
public enum PaymentMethod
{
    ON_BOARD(0, "车上支付"),
    BEFORE_BOARDING(1, "站前支付");

    private final int code;
    private final String name;

    PaymentMethod(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
}
