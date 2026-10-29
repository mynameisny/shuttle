package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 区段合并规则（对应 GTFS fare_leg_join_rules.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#fare_leg_join_rulestxt">fare_leg_join_rules.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 定义换乘匹配规则，使两条连续区段被视为单一有效票程（Fares V2）。
 * - from_network_id (Foreign ID) [必填]     换乘前区段所属路线网络。
 * - to_network_id   (Foreign ID) [必填]     换乘后区段所属路线网络。
 * - from_stop_id    (Foreign ID) [条件必填] 换乘前区段的结束站点（定义 to_stop_id 时必填）。
 * - to_stop_id      (Foreign ID) [条件必填] 换乘后区段的出发站点（定义 from_stop_id 时必填）。
 * 注：引用字段为弱引用（GTFS CSV 语义），不用数据库外键。
 * </pre>
 */
@Entity(name = "fare_leg_join_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareLegJoinRule extends AbstractAuditable
{
    @Column(name = "from_network_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '换乘前区段路线网络'")
    private String fromNetworkId;
    @Column(name = "to_network_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '换乘后区段路线网络'")
    private String toNetworkId;
    @Column(name = "from_stop_id", columnDefinition = "VARCHAR(50) COMMENT '换乘前区段结束站点，引用 stops.stop_id'")
    private String fromStopId;
    @Column(name = "to_stop_id", columnDefinition = "VARCHAR(50) COMMENT '换乘后区段出发站点，引用 stops.stop_id'")
    private String toStopId;
}
