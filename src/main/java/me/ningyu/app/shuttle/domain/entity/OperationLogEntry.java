package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.GPSPriority;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <pre>
 * 运营记录实体
 * 记录班次在各个站点的实际停靠和出发时间
 * 数据来源：
 * 1. 班车端GPS上报（含站点到达事件）
 * 2. 乘客扫码（间接推断车辆位置）
 *
 * TODO: 集成移动端GPS上报功能
 * TODO: 实现基于GPS的站点到达检测逻辑
 * </pre>
 */
@Entity(name = "operation_log_entry")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogEntry extends AbstractAuditable
{
    /**
     * 关联的班次
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false, columnDefinition = "BIGINT COMMENT '班次ID'")
    private Shift shift;

    /**
     * 物理站点
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '物理站点ID'")
    private PhysicalStop stop;

    /**
     * 实际到达时间
     */
    @Column(name = "actual_arrival_time", columnDefinition = "DATETIME COMMENT '实际到达时间'")
    private LocalDateTime actualArrivalTime;

    /**
     * 实际出发时间
     * 默认逻辑：如果未手动上报，则 = 到达时间 + 1分钟
     */
    @Column(name = "actual_departure_time", columnDefinition = "DATETIME COMMENT '实际出发时间'")
    private LocalDateTime actualDepartureTime;

    /**
     * GPS纬度
     */
    @Column(name = "latitude", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT 'GPS纬度'")
    private BigDecimal latitude;

    /**
     * GPS经度
     */
    @Column(name = "longitude", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT 'GPS经度'")
    private BigDecimal longitude;

    /**
     * GPS数据优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gps_priority", columnDefinition = "VARCHAR(50) COMMENT 'GPS数据优先级'")
    private GPSPriority gpsPriority;

    /**
     * 数据来源描述
     */
    @Column(name = "source", columnDefinition = "VARCHAR(100) COMMENT '数据来源描述，如\"司机手动上报\"、\"乘客扫码\"'")
    private String source;

    /**
     * 备注
     */
    @Column(columnDefinition = "TEXT COMMENT '备注，如晚点原因等'")
    private String remark;

    /**
     * 便捷方法：设置实际到达时间，并自动计算出发时间（如果未设置）
     */
    public void setActualArrivalTimeWithDefaultDeparture(LocalDateTime arrivalTime)
    {
        this.actualArrivalTime = arrivalTime;
        if (this.actualDepartureTime == null)
        {
            // 默认：出发时间 = 到达时间 + 1分钟
            this.actualDepartureTime = arrivalTime.plusMinutes(1);
        }
    }
}
