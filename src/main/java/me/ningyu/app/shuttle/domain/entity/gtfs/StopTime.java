package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.ContinuousDropType;
import me.ningyu.app.shuttle.enums.PickupDropOffType;
import me.ningyu.app.shuttle.enums.Timepoint;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * <pre>
 * 停站时刻（对应 GTFS stop_times.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#stop_timestxt">stop_times.txt</a>
 *
 * 主键：(trip_id, stop_sequence)
 * stop_id / location_group_id / location_id 三者互斥，至少定义其一（常规场景为 stop_id）
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - trip_id                     (Foreign ID)           [必填]     所属班次。
 * - arrival_time                (Time)                 [条件必填] 到站时间（首末站/timepoint=1 必填）。
 * - departure_time              (Time)                 [条件必填] 发车时间。
 * - stop_id                     (Foreign ID)           [条件必填] 停靠站点（location_group_id / location_id 未定义时）。
 * - location_group_id           (Foreign ID)           [条件禁止] 位置组（按需服务，stop_id / location_id 已定义时禁止）。
 * - location_id                 (Foreign ID)           [条件禁止] GeoJSON位置区域（按需服务，stop_id / location_group_id 已定义时禁止）。
 * - stop_sequence               (Non-negative integer) [必填]     站点顺序（可非连续，但须递增）。
 * - stop_headsign               (Text)                 [可选]     站点头牌文字，覆盖 trips.trip_headsign。
 * - start_pickup_drop_off_window(Time)                 [条件必填] 按需服务开始时间。
 * - end_pickup_drop_off_window  (Time)                 [条件必填] 按需服务结束时间。
 * - pickup_type                 (Enum)                 [条件禁止] 上车方式（0=正常,1=不可,2=电话预约,3=司机协商）。
 * - drop_off_type               (Enum)                 [条件禁止] 下车方式（同上）。
 * - continuous_pickup           (Enum)                 [条件禁止] 连续上车，覆盖 routes.continuous_pickup。
 * - continuous_drop_off         (Enum)                 [条件禁止] 连续下车，覆盖 routes.continuous_drop_off。
 * - shape_dist_traveled         (Non-negative float)   [可选]     沿 shape 的实际行驶距离。
 * - timepoint                   (Enum)                 [可选]     时间精度（0=近似,1=精确）。
 * - pickup_booking_rule_id      (Foreign ID)           [可选]     上车预订规则。
 * - drop_off_booking_rule_id    (Foreign ID)           [可选]     下车预订规则。
 *
 * Java 字段映射：
 * - trip_id                     → trip
 * - arrival_time                → arrivalTime
 * - departure_time              → departureTime
 * - stop_id                     → stop
 * - location_group_id           → locationGroup
 * - location_id                 → locationId
 * - stop_sequence               → stopSequence
 * - stop_headsign               → stopHeadsign
 * - start_pickup_drop_off_window→ startPickupDropOffWindow
 * - end_pickup_drop_off_window  → endPickupDropOffWindow
 * - pickup_type                 → pickupType
 * - drop_off_type               → dropOffType
 * - continuous_pickup           → continuousPickup
 * - continuous_drop_off         → continuousDropOff
 * - shape_dist_traveled         → shapeDistTraveled
 * - timepoint                   → timepoint
 * - pickup_booking_rule_id      → pickupBookingRuleId
 * - drop_off_booking_rule_id    → dropOffBookingRuleId
 * </pre>
 */
@Entity(name = "stop_time")
@Table(name = "stop_time")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopTime extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", referencedColumnName = "trip_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '所属行程'")
    @JsonBackReference
    private Trip trip;

    /**
     * 停靠站点。
     * GTFS 规范：与 location_group_id / location_id 互斥，三者至少定义其一，常规场景为 stop_id。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", referencedColumnName = "stop_id", columnDefinition = "VARCHAR(50) COMMENT '停靠站点（与location_group_id/location_id互斥）'")
    private Stop stop;

    /**
     * 位置组（按需服务场景）。
     * 与 stop_id / location_id 互斥。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_group_id", referencedColumnName = "location_group_id", columnDefinition = "VARCHAR(50) COMMENT '位置组（与stop_id/location_id互斥）'")
    private LocationGroup locationGroup;

    /**
     * GeoJSON 位置区域标识（按需服务场景，引用 locations.geojson 的 id）。
     * 与 stop_id / location_group_id 互斥。
     */
    @Column(name = "location_id", columnDefinition = "VARCHAR(50) COMMENT 'GeoJSON位置区域ID（与stop_id/location_group_id互斥）'")
    private String locationId;

    @Column(name = "arrival_time", columnDefinition = "TIME COMMENT '到站时间（服务日午夜后可大于24:00:00）'")
    private LocalTime arrivalTime;

    @Column(name = "departure_time", columnDefinition = "TIME COMMENT '离站时间'")
    private LocalTime departureTime;

    @Column(name = "stop_sequence", nullable = false, columnDefinition = "INT COMMENT '行程中的站点顺序（递增，可不连续）'")
    private Integer stopSequence;

    @Column(name = "stop_headsign", columnDefinition = "VARCHAR(100) COMMENT '站点头牌文字，覆盖 trips.trip_headsign'")
    private String stopHeadsign;

    /**
     * 按需服务开始时间。
     * 定义后与 arrival_time / departure_time 互斥。
     */
    @Column(name = "start_pickup_drop_off_window", columnDefinition = "TIME COMMENT '按需服务上/下车时间窗开始'")
    private LocalTime startPickupDropOffWindow;

    /**
     * 按需服务结束时间。
     */
    @Column(name = "end_pickup_drop_off_window", columnDefinition = "TIME COMMENT '按需服务上/下车时间窗结束'")
    private LocalTime endPickupDropOffWindow;

    @Enumerated(EnumType.STRING)
    @Column(name = "pickup_type", columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '上车方式（SCHEDULED/NONE/PHONE_AGENCY/COORDINATE_WITH_DRIVER）'")
    @Builder.Default
    private PickupDropOffType pickupType = PickupDropOffType.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "drop_off_type", columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '下车方式（SCHEDULED/NONE/PHONE_AGENCY/COORDINATE_WITH_DRIVER）'")
    @Builder.Default
    private PickupDropOffType dropOffType = PickupDropOffType.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "continuous_pickup", columnDefinition = "VARCHAR(20) DEFAULT 'NOT_ALLOWED' COMMENT '连续上车规则，覆盖 routes 中的定义'")
    @Builder.Default
    private ContinuousDropType continuousPickup = ContinuousDropType.NOT_ALLOWED;

    @Enumerated(EnumType.STRING)
    @Column(name = "continuous_drop_off", columnDefinition = "VARCHAR(20) DEFAULT 'NOT_ALLOWED' COMMENT '连续下车规则，覆盖 routes 中的定义'")
    @Builder.Default
    private ContinuousDropType continuousDropOff = ContinuousDropType.NOT_ALLOWED;

    @Column(name = "shape_dist_traveled", precision = 10, scale = 2, columnDefinition = "DECIMAL(10,2) COMMENT '沿形状从起点到该站的实际行驶距离'")
    private BigDecimal shapeDistTraveled;

    @Enumerated(EnumType.STRING)
    @Column(name = "timepoint", columnDefinition = "VARCHAR(20) DEFAULT 'EXACT' COMMENT '时间精度：APPROXIMATE=近似，EXACT=精确'")
    @Builder.Default
    private Timepoint timepoint = Timepoint.EXACT;

    @Column(name = "pickup_booking_rule_id", columnDefinition = "VARCHAR(50) COMMENT '上车预订规则标识'")
    private String pickupBookingRuleId;

    @Column(name = "drop_off_booking_rule_id", columnDefinition = "VARCHAR(50) COMMENT '下车预订规则标识'")
    private String dropOffBookingRuleId;
}
