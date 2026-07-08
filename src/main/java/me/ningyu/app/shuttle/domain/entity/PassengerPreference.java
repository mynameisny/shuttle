package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 乘客偏好设置
 * 记录乘客的常坐线路和站点
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false, columnDefinition = "BIGINT COMMENT '乘客ID'")
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '线路方向ID'")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '停靠点ID'")
    private Stop stop;

    @Column(name = "is_primary", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否为默认主要偏好'")
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(columnDefinition = "TEXT COMMENT '描述'")
    private String description;
}
