package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 票价区段规则（对应 GTFS fare_leg_rules.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#fare_leg_rulestxt">fare_leg_rules.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 定义单个出行区段的票价规则（Fares V2）。
 * - leg_group_id            (ID)         [可选] 区段组标识，被 fare_transfer_rules 引用。
 * - network_id              (Foreign ID) [可选] 适用的路线网络（引用 routes.network_id 或 networks.network_id）。
 * - from_area_id            (Foreign ID) [可选] 出发区域（引用 areas）。
 * - to_area_id              (Foreign ID) [可选] 到达区域（引用 areas）。
 * - from_timeframe_group_id (Foreign ID) [可选] 起点验票时间框架组（引用 timeframes）。
 * - to_timeframe_group_id   (Foreign ID) [可选] 终点验票时间框架组（引用 timeframes）。
 * - fare_product_id         (Foreign ID) [必填] 该区段所需的票价产品（引用 fare_products）。
 * - rule_priority           (Non-neg int)[可选] 规则匹配优先级，多规则匹配时取最高值，空值视为0。
 * 注：引用字段为弱引用（GTFS CSV 语义），不用数据库外键。
 * </pre>
 */
@Entity(name = "fare_leg_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareLegRule extends AbstractAuditable
{
    @Column(name = "leg_group_id", columnDefinition = "VARCHAR(50) COMMENT '区段组标识'")
    private String legGroupId;
    @Column(name = "network_id", columnDefinition = "VARCHAR(50) COMMENT '适用路线网络，引用 routes.network_id 或 networks.network_id'")
    private String networkId;
    @Column(name = "from_area_id", columnDefinition = "VARCHAR(50) COMMENT '出发区域，引用 areas.area_id'")
    private String fromAreaId;
    @Column(name = "to_area_id", columnDefinition = "VARCHAR(50) COMMENT '到达区域，引用 areas.area_id'")
    private String toAreaId;
    @Column(name = "from_timeframe_group_id", columnDefinition = "VARCHAR(50) COMMENT '起点验票时间框架组，引用 timeframes.timeframe_group_id'")
    private String fromTimeframeGroupId;
    @Column(name = "to_timeframe_group_id", columnDefinition = "VARCHAR(50) COMMENT '终点验票时间框架组，引用 timeframes.timeframe_group_id'")
    private String toTimeframeGroupId;
    @Column(name = "fare_product_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '所需票价产品，引用 fare_products'")
    private String fareProductId;
    @Column(name = "rule_priority", columnDefinition = "INT COMMENT '规则匹配优先级，最高值优先，空值视为0'")
    private Integer rulePriority;
}
