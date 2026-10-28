package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.BikeAllowance;
import me.ningyu.app.shuttle.enums.CarAllowance;
import me.ningyu.app.shuttle.enums.WheelchairAccessibility;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 班次（对应 GTFS trips.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#tripstxt">trips.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - route_id              (Foreign ID)  [必填]     所属线路。
 * - service_id            (Foreign ID)  [必填]     服务日历，标识班次可用日期集合。
 * - trip_id               (Unique ID)   [必填]     班次唯一标识。
 * - trip_headsign         (Text)        [可选]     车上头牌显示的终点文字。
 * - trip_short_name       (Text)        [可选]     班次对外简称（如列车车次号）。
 * - direction_id          (Enum)        [可选]     行驶方向（0=正向/出城,1=反向/进城）。
 * - block_id              (ID)          [可选]     车辆组ID，同一车辆连续执行的多个班次。
 * - shape_id              (Foreign ID)  [条件必填] 描述行驶路径的形状，连续上下车时必填。
 * - wheelchair_accessible (Enum)        [可选]     无障碍信息（0/1/2）。
 * - bikes_allowed         (Enum)        [可选]     是否允许携带自行车（0/1/2）。
 * - cars_allowed          (Enum)        [可选]     是否允许搭载汽车（0/1/2）。
 * - safe_duration_factor  (Float)       [可选]     按需行程估算时间的乘数，安全时间 = factor × 驾驶时间 + offset。
 * - safe_duration_offset  (Float)       [可选]     按需行程估算时间的固定偏移量（秒）。
 *
 * Java 字段映射：
 * - route_id              → route
 * - service_id            → serviceId
 * - trip_id               → tripId
 * - trip_headsign         → tripHeadsign
 * - trip_short_name       → tripShortName
 * - direction_id          → directionId
 * - block_id              → blockId
 * - shape_id              → shapeId
 * - wheelchair_accessible → wheelchairAccessible
 * - bikes_allowed         → bikesAllowed
 * - cars_allowed          → carsAllowed
 * - safe_duration_factor  → safeDurationFactor
 * - safe_duration_offset  → safeDurationOffset
 * </pre>
 */
@Entity(name = "trip")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属线路'")
    private Route route;

    @Column(name = "trip_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '行程标识，如 463_0700_out'")
    private String tripId;

    @Column(name = "service_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '服务日历标识，引用 ServiceCalendar.serviceId'")
    private String serviceId;

    @Column(name = "direction_id", columnDefinition = "INT DEFAULT 0 COMMENT '方向ID（0=正向，1=反向）'")
    @Builder.Default
    private Integer directionId = 0;

    @Column(name = "trip_headsign", columnDefinition = "VARCHAR(100) COMMENT '车头方向牌文字，向乘客标识行程目的地'")
    private String tripHeadsign;

    @Column(name = "trip_short_name", columnDefinition = "VARCHAR(50) COMMENT '行程简称，如火车车次号'")
    private String tripShortName;

    @Column(name = "block_id", columnDefinition = "VARCHAR(50) COMMENT '车辆组标识，同一车辆连续执行的多个行程'")
    private String blockId;

    @Column(name = "shape_id", columnDefinition = "VARCHAR(50) COMMENT '行驶路径形状标识'")
    private String shapeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wheelchair_accessible", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '轮椅无障碍信息（NO_INFO/ACCESSIBLE/NOT_ACCESSIBLE）'")
    @Builder.Default
    private WheelchairAccessibility wheelchairAccessible = WheelchairAccessibility.NO_INFO;

    @Enumerated(EnumType.STRING)
    @Column(name = "bikes_allowed", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '自行车规定（NO_INFO/ALLOWED/NOT_ALLOWED）'")
    @Builder.Default
    private BikeAllowance bikesAllowed = BikeAllowance.NO_INFO;

    /**
     * 是否允许搭载汽车。
     * 0 - 无信息
     * 1 - 允许
     * 2 - 禁止
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "cars_allowed", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '汽车搭载规定（NO_INFO/ALLOWED/NOT_ALLOWED）'")
    @Builder.Default
    private CarAllowance carsAllowed = CarAllowance.NO_INFO;

    /**
     * 按需行程估算时间乘数。
     * SafeTravelDuration = safeDurationFactor × DrivingDuration + safeDurationOffset
     */
    @Column(name = "safe_duration_factor", columnDefinition = "DOUBLE COMMENT '按需行程时间估算乘数'")
    private Double safeDurationFactor;

    /**
     * 按需行程估算时间固定偏移量（秒）。
     * SafeTravelDuration = safeDurationFactor × DrivingDuration + safeDurationOffset
     */
    @Column(name = "safe_duration_offset", columnDefinition = "DOUBLE COMMENT '按需行程时间估算固定偏移量（秒）'")
    private Double safeDurationOffset;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<StopTime> stopTimes = new ArrayList<>();
}
