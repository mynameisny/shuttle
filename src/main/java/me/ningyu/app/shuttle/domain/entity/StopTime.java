package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.Timepoint;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * <pre>
 * 停站时刻（对应 GTFS stop_times.txt）
 * 表示某 Trip 第几个经过哪个 Stop，及到站/发车时间
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#stop_timestxt">stop_times.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - trip_id            (Foreign ID)           [必填]     所属班次。
 * - arrival_time       (Time)                 [条件必填] 到站时间（首末站/timepoint=1 必填）。
 * - departure_time     (Time)                 [条件必填] 发车时间。
 * - stop_id            (Foreign ID)           [条件必填] 停靠站点。
 * - stop_sequence      (Non-negative integer) [必填]     站点顺序（可非连续，但须递增）。
 * - stop_headsign      (Text)                 [可选]     站点头牌文字。
 * - pickup_type        (Enum)                 [条件禁止] 上车方式（0=常规,1=不可上车,2=电话预约,3=司机协调）。
 * - drop_off_type      (Enum)                 [条件禁止] 下车方式（0=常规,1=不可下车,2=电话预约,3=司机协调）。
 * - continuous_pickup  (Enum)                 [条件禁止] 连续上车。
 * - continuous_drop_off(Enum)                 [条件禁止] 连续下车。
 * - shape_dist_traveled(Non-negative float)   [可选]     距起点的实际距离。
 * - timepoint          (Enum)                 [可选]     时间是否精确（0=近似,1=精确）。
 *
 * Java 字段映射：
 * - trip_id            → trip
 * - arrival_time       → arrivalTime
 * - departure_time     → departureTime
 * - stop_id            → stop
 * - stop_sequence      → stopSequence
 * - stop_headsign      → stopHeadsign
 * - pickup_type        → pickupType
 * - drop_off_type      → dropOffType
 * - continuous_pickup  → continuousPickup
 * - continuous_drop_off → continuousDropOff
 * - shape_dist_traveled → shapeDistTraveled
 * - timepoint          → timepoint
 * </pre>
 */
@Entity(name = "stop_time")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopTime extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属班次ID（对应GTFS trip_id）'")
    @JsonBackReference
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '停靠点ID（对应GTFS stop_id）'")
    private Stop stop;

    @Column(name = "arrival_time", columnDefinition = "TIME COMMENT '到站时间（对应GTFS arrival_time）'")
    private LocalTime arrivalTime;

    @Column(name = "departure_time", columnDefinition = "TIME COMMENT '发车时间（对应GTFS departure_time）'")
    private LocalTime departureTime;

    @Column(name = "stop_sequence", nullable = false, columnDefinition = "INT COMMENT '站点顺序（对应GTFS stop_sequence，可非连续但须递增）'")
    private Integer stopSequence;

    @Column(name = "stop_headsign", columnDefinition = "VARCHAR(100) COMMENT '站点头牌文字（对应GTFS stop_headsign）'")
    private String stopHeadsign;

    @Column(name = "pickup_type", columnDefinition = "INT DEFAULT 0 COMMENT '上车类型（对应GTFS pickup_type）：0=常规，1=不可上车，2=电话预约，3=司机协调'")
    @Builder.Default
    private Integer pickupType = 0;

    @Column(name = "drop_off_type", columnDefinition = "INT DEFAULT 0 COMMENT '下车类型（对应GTFS drop_off_type）：0=常规，1=不可下车，2=电话预约，3=司机协调'")
    @Builder.Default
    private Integer dropOffType = 0;

    @Column(name = "continuous_pickup", columnDefinition = "INT DEFAULT 1 COMMENT '连续上车（对应GTFS continuous_pickup）：0=允许，1=不允许'")
    @Builder.Default
    private Integer continuousPickup = 1;

    @Column(name = "continuous_drop_off", columnDefinition = "INT DEFAULT 1 COMMENT '连续下车（对应GTFS continuous_drop_off）：0=允许，1=不允许'")
    @Builder.Default
    private Integer continuousDropOff = 1;

    @Column(name = "shape_dist_traveled", columnDefinition = "DECIMAL(10,2) COMMENT '距起点的实际距离（对应GTFS shape_dist_traveled）'")
    private BigDecimal shapeDistTraveled;

    @Enumerated(EnumType.STRING)
    @Column(name = "timepoint", columnDefinition = "VARCHAR(20) DEFAULT 'EXACT' COMMENT '时间精度（对应GTFS timepoint）：APPROXIMATE=近似，EXACT=精确'")
    @Builder.Default
    private Timepoint timepoint = Timepoint.EXACT;
}