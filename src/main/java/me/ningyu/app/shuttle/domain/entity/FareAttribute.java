package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.PaymentMethod;
import me.ningyu.app.shuttle.enums.TransferPermission;

import java.math.BigDecimal;

/**
 * <pre>
 * 票价属性（对应 GTFS fare_attributes.txt, Fares V1）
 * 定义票价规则，包括价格、货币、支付方式、换乘规则等
 *
 * 参考：https://gtfs.org/documentation/schedule/reference/#fare_attributestxt
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - fare_id           (Unique ID)           [必填]     票价标识。
 * - price             (Non-negative float)  [必填]     票价金额。
 * - currency_type     (Currency code)       [必填]     货币类型（如 CNY、USD）。
 * - payment_method    (Enum)                [必填]     支付方式（0=车上,1=站前）。
 * - transfers         (Enum)                [必填]     允许换乘次数（0/1/2/空=无限）。
 * - agency_id         (Foreign ID)          [条件必填] 所属机构。
 * - transfer_duration (Non-negative integer)[可选]     换乘有效时长（秒）。
 * </pre>
 */
@Entity(name = "fare_attribute")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareAttribute extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属机构ID'")
    private Agency agency;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '票价编码'")
    private String code;

    @Column(nullable = false, precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) COMMENT '票价金额'")
    private BigDecimal price;

    @Column(name = "currency_type", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'CNY' COMMENT '货币类型，如CNY、USD'")
    @Builder.Default
    private String currencyType = "CNY";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '支付方式：ON_BOARD=车上，BEFORE_BOARDING=站前'")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfers", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '允许换乘次数'")
    private TransferPermission transfers;

    @Column(name = "transfer_duration", columnDefinition = "INT COMMENT '换乘有效时长(秒)'")
    private Integer transferDuration;
}
