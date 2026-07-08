package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <pre>
 * 运营记录实体
 * 记录班次在各个站点的实际停靠和出发时间
 * 数据来源：
 * 1. 班车端GPS上报（含站点到达事件）
 * 2. 乘客扫码（间接推断车辆位置）
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false, columnDefinition = "BIGINT COMMENT '班次ID'")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '停靠点ID'")
    private Stop stop;

    @Column(name = "actual_arrival_time", columnDefinition = "DATETIME COMMENT '实际到达时间'")
    private LocalDateTime actualArrivalTime;

    @Column(name = "actual_departure_time", columnDefinition = "DATETIME COMMENT '实际出发时间'")
    private LocalDateTime actualDepartureTime;

    @Column(name = "latitude", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT 'GPS纬度'")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT 'GPS经度'")
    private BigDecimal longitude;

    @Column(name = "source", columnDefinition = "VARCHAR(100) COMMENT '数据来源描述'")
    private String source;

    @Column(columnDefinition = "TEXT COMMENT '备注'")
    private String remark;

    public void setActualArrivalTimeWithDefaultDeparture(LocalDateTime arrivalTime)
    {
        this.actualArrivalTime = arrivalTime;
        if (this.actualDepartureTime == null)
        {
            this.actualDepartureTime = arrivalTime.plusMinutes(1);
        }
    }
}
