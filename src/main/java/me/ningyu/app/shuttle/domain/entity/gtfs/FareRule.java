package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 票价规则（对应 GTFS fare_rules.txt, Fares V1）
 * 定义票价适用的线路和区域
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#fare_rulestxt">...</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - fare_id        (Foreign ID) [必填] 票价标识。
 * - route_id       (Foreign ID) [可选] 适用线路。
 * - origin_id      (Foreign ID) [可选] 起点区域（zone_id）。
 * - destination_id (Foreign ID) [可选] 终点区域。
 * - contains_id    (Foreign ID) [可选] 途经区域。
 * </pre>
 */
@Entity(name = "fare_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareRule extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fare_id", referencedColumnName = "fare_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '所属票价'")
    private FareAttribute fareAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", columnDefinition = "VARCHAR(50) COMMENT '适用线路'")
    private Route route;

    @Column(name = "origin_id", columnDefinition = "VARCHAR(50) COMMENT '起点票价区域'")
    private String originId;

    @Column(name = "destination_id", columnDefinition = "VARCHAR(50) COMMENT '终点票价区域'")
    private String destinationId;

    @Column(name = "contains_id", columnDefinition = "VARCHAR(50) COMMENT '途经票价区域'")
    private String containsId;
}
