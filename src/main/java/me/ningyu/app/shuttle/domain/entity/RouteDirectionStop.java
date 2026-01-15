package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * <pre>
 * 线路方向停靠点
 * </pre>
 */
@Entity(name = "route_direction_stop")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDirectionStop extends AbstractAuditable
{
    /**
     * 所属的带方向线路
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_direction_id", nullable = false, columnDefinition = "BIGINT COMMENT '带方向的线路ID'")
    private RouteDirection directionalRoute;

    /**
     * 停靠的物理站点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '物理站点的ID'")
    private Stop stop;

    /**
     * 计划到达时间（可为空，首站通常无到达时间）
     */
    @Column(name = "arrival_time", columnDefinition = "TIME COMMENT '计划到达时间'")
    private LocalTime arrivalTime;

    /**
     * 计划出发时间（可为空，末站通常无出发时间）
     */
    @Column(name = "departure_time", columnDefinition = "TIME COMMENT '计划出发时间'")
    private LocalTime departureTime;

    /**
     * 站点顺序，从 1 开始递增，用于确定停靠先后
     */
    @Column(name = "sequence", columnDefinition = "INT(11) COMMENT '站点顺序（从1开始）'")
    private Integer sequence;

    /**
     * 描述
     */
    @Column(columnDefinition = "TEXT COMMENT '描述'")
    private String description;
}
