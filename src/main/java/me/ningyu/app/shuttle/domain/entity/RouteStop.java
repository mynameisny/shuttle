package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * <pre>
 * 路线站点（对应 GTFS stop_times 中停站顺序）
 * 表示某 Route 第几个经过哪个 Stop
 *
 * 去程和返程的 RouteStop 不要求一一对应
 * </pre>
 */
@Entity(name = "route_stop")
@Table(name = "route_direction_stop")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteStop extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '线路方向ID'")
    @JsonBackReference
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '停靠点ID'")
    private Stop stop;

    @Column(nullable = false, columnDefinition = "INT COMMENT '站点顺序（从1开始）'")
    private Integer sequence;

    @Column(name = "distance_from_start", columnDefinition = "DECIMAL(10,2) COMMENT '距起点的距离(米)'")
    private BigDecimal distanceFromStart;
}
