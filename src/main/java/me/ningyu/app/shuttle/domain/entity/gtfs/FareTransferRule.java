package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.DurationLimitType;

/**
 * <pre>
 * 票价换乘规则（对应 GTFS fare_transfer_rules.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#fare_transfer_rulestxt">fare_transfer_rules.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 定义区段之间换乘的票价规则（Fares V2），规则有方向性，反向不适用。
 * - from_leg_group_id  (Foreign ID) [条件必填] 换乘前区段组（引用 fare_leg_rules.leg_group_id）。
 * - to_leg_group_id    (Foreign ID) [条件必填] 换乘后区段组。
 * - fare_product_id    (Foreign ID) [条件必填] 换乘所需票价产品。
 * - transfer_count     (Enum)       [条件禁止] 允许换乘次数（-1=不允许,0=不限,2=最多2次,空=不限）。
 * - duration_limit     (Pos. integer)[条件禁止] 换乘有效时长（秒）。
 * - duration_limit_type(Enum)       [可选]     时限计时锚点（0=前段出发,1=前段到达,2=后段出发,3=后段到达），默认1。
 * </pre>
 */
@Entity(name = "fare_transfer_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareTransferRule extends AbstractAuditable
{
    @Column(name = "from_leg_group_id", columnDefinition = "VARCHAR(50) COMMENT '换乘前区段组，引用 fare_leg_rules.leg_group_id'")
    private String fromLegGroupId;
    @Column(name = "to_leg_group_id", columnDefinition = "VARCHAR(50) COMMENT '换乘后区段组，引用 fare_leg_rules.leg_group_id'")
    private String toLegGroupId;
    @Column(name = "fare_product_id", columnDefinition = "VARCHAR(50) COMMENT '换乘所需票价产品'")
    private String fareProductId;
    @Column(name = "transfer_count", columnDefinition = "INT COMMENT '允许换乘次数（-1=不允许，0或空=不限）'")
    private Integer transferCount;
    @Column(name = "duration_limit", columnDefinition = "INT COMMENT '换乘有效时长（秒）'")
    private Integer durationLimit;
    /**
     * 换乘时限计时锚点。
     * FROM_DEPARTURE - 换乘前区段的出发时刻
     * FROM_ARRIVAL - 换乘前区段的到达时刻（默认）
     * TO_DEPARTURE - 换乘后区段的出发时刻
     * TO_ARRIVAL - 换乘后区段的到达时刻
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "duration_limit_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'FROM_ARRIVAL' COMMENT '时限计时锚点'")
    @Builder.Default
    private DurationLimitType durationLimitType = DurationLimitType.FROM_ARRIVAL;
}
