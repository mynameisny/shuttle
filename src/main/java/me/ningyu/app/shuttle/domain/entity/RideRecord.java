package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <pre>
 * 乘坐记录实体
 * 记录乘客的每次乘车行为
 * </pre>
 */
@Entity(name = "ride_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRecord extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", columnDefinition = "BIGINT COMMENT '乘客ID'")
    private Passenger passenger;

    @Column(name = "passenger", columnDefinition = "VARCHAR(100) COMMENT '乘客姓名（兼容旧数据）'")
    private String passengerName;

    @Column(name = "aboard_time", nullable = false, columnDefinition = "DATETIME COMMENT '上车时间'")
    private LocalDateTime aboardTime;

    @Column(name = "disembark_time", columnDefinition = "DATETIME COMMENT '下车时间'")
    private LocalDateTime disembarkTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;
}
