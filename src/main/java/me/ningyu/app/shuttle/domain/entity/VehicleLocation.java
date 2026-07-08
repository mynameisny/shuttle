package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * <pre>
 * 车辆位置（实时定位）
 * 对应 GTFS-Realtime 的 VehiclePosition
 *
 * 用于：
 * - 地图展示
 * - ETA（到站预测）
 * - 偏航检测
 * - 历史轨迹
 * </pre>
 */
@Entity(name = "vehicle_location")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocation extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, columnDefinition = "BIGINT COMMENT '车辆ID'")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", columnDefinition = "BIGINT COMMENT '班次ID'")
    private Trip trip;

    @Column(nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '经度'")
    private BigDecimal longitude;

    @Column(nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '纬度'")
    private BigDecimal latitude;

    @Column(columnDefinition = "DECIMAL(10,2) COMMENT '速度(km/h)'")
    private BigDecimal speed;

    @Column(columnDefinition = "DECIMAL(5,2) COMMENT '行驶方向角度(0-360)'")
    private BigDecimal heading;

    @Column(name = "gps_time", nullable = false, columnDefinition = "DATETIME COMMENT 'GPS采集时间'")
    private java.time.LocalDateTime gpsTime;
}
