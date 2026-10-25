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
 * 参考：https://gtfs.org/documentation/schedule/reference/#stop_timestxt
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - trip_id            (Foreign ID)           [必填]     所属班次。
 * - arrival_time       (Time)                 [条件必填] 到站时间（首末站/timepoint=1 必填）。
 * - departure_time     (Time)                 [条件必填] 发车时间。
 * - stop_id            (Foreign ID)           [条件必填] 停靠站点。
 * - stop_sequence      (Non-negative integer) [必填]     站点顺序（可非连续，但须递增）。
 * - stop_headsign      (Text)                 [可选]     站点头牌文字。
 * - pickup_type        (Enum)                 [条件禁止] 上车方式（0/1/2/3）。
 * - drop_off_type      (Enum)                 [条件禁止] 下车方式（0/1/2/3）。
 * - continuous_pickup  (Enum)                 [条件禁止] 连续上车。
 * - continuous_drop_off(Enum)                 [条件禁止] 连续下车。
 * - shape_dist_traveled(Non-negative float)   [可选]     距起点的实际距离。
 * - timepoint          (Enum)                 [可选]     时间是否精确（0=近似,1=精确）。
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

    @Column(name = "arrival_time", columnDefinition = "TIME COMMENT '计划到达时间'")
    private LocalTime arrivalTime;

    @Column(name = "departure_time", columnDefinition = "TIME COMMENT '计划出发时间'")
    private LocalTime departureTime;

    @Column(name = "distance_from_start", columnDefinition = "DECIMAL(10,2) COMMENT '距起点的距离(米)'")
    private BigDecimal distanceFromStart;

    @Enumerated(EnumType.STRING)
    @Column(name = "timepoint", columnDefinition = "VARCHAR(20) DEFAULT 'EXACT' COMMENT '时间精度：APPROXIMATE=近似，EXACT=精确'")
    @Builder.Default
    private Timepoint timepoint = Timepoint.EXACT;

    @Column(name = "stop_headsign", columnDefinition = "VARCHAR(100) COMMENT '站点头牌文字'")
    private String stopHeadsign;

    @Column(name = "pickup_type", columnDefinition = "INT DEFAULT 0 COMMENT '上车类型：0=常规，1=不可上车，2=电话预约，3=司机协调'")
    @Builder.Default
    private Integer pickupType = 0;

    @Column(name = "drop_off_type", columnDefinition = "INT DEFAULT 0 COMMENT '下车类型：0=常规，1=不可下车，2=电话预约，3=司机协调'")
    @Builder.Default
    private Integer dropOffType = 0;

    @Column(name = "continuous_pickup", columnDefinition = "INT DEFAULT 1 COMMENT '连续上车：0=允许，1=不允许'")
    @Builder.Default
    private Integer continuousPickup = 1;

    @Column(name = "continuous_drop_off", columnDefinition = "INT DEFAULT 1 COMMENT '连续下车：0=允许，1=不允许'")
    @Builder.Default
    private Integer continuousDropOff = 1;
}
