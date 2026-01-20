package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <pre>
 * 乘坐记录实体
 * 记录乘客的每次乘车行为
 *
 * TODO: 集成 Kafka 消息队列
 * - 乘客扫码上车时，向 Kafka 发送 RideStartedEvent
 * - Topic: ride_started
 * - 用于 HR 考勤系统："上车即打卡"
 *
 * TODO: 移动端集成
 * - 乘客端扫描班车 QR Code（静态贴纸或动态码）
 * - 触发上车事件 → 写入 RideRecord + 发送 Kafka 消息
 * - 乘客扫码时可附带 GPS 数据（用于车辆定位）
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
    /**
     * 乘客
     * TODO: 改为关联 Passenger 实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", columnDefinition = "BIGINT COMMENT '乘客ID'")
    private Passenger passenger;

    /**
     * 临时字段：乘客姓名（兼容旧数据）
     * TODO: 迁移完成后可删除
     */
    @Column(name = "passenger", columnDefinition = "VARCHAR(100) COMMENT '乘客（兼容旧数据）'")
    private String passengerName;

    /**
     * 上车时间（必填，由扫码触发）
     */
    @Column(name = "aboard_time", nullable = false, columnDefinition = "DATETIME COMMENT '上车时间'")
    private LocalDateTime aboardTime;

    /**
     * 下车时间（可选，因下午不扫码）
     */
    @Column(name = "disembark_time", columnDefinition = "DATETIME COMMENT '下车时间'")
    private LocalDateTime disembarkTime;

    /**
     * 关联的班次
     * TODO: 重命名字段为 shift
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Shift schedule;
}
