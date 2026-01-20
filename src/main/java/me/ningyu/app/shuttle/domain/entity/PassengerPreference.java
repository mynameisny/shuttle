package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 乘客偏好设置
 * 记录乘客的常坐线路和站点
 * 一个乘客可以有多个偏好，其中一个可以标记为"默认主要偏好"
 * </pre>
 */
@Entity(name = "passenger_preference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerPreference extends AbstractAuditable
{
    /**
     * 所属乘客
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false, columnDefinition = "BIGINT COMMENT '乘客ID'")
    private Passenger passenger;

    /**
     * 常坐线路方向
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_direction_id", nullable = false, columnDefinition = "BIGINT COMMENT '线路方向ID'")
    private RouteDirection routeDirection;

    /**
     * 常坐站点（物理站点）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '物理站点ID'")
    private PhysicalStop stop;

    /**
     * 是否为默认主要偏好
     * 用于满载率与压力分析
     */
    @Column(name = "is_primary", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否为默认主要偏好'")
    private Boolean isPrimary = false;

    /**
     * 描述（可选）
     */
    @Column(columnDefinition = "TEXT COMMENT '描述,如:工作日早上上班路线'")
    private String description;
}
