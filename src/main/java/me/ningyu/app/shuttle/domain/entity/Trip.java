package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.BikeAllowance;
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
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属线路ID（对应GTFS route_id）'")
    private Route route;

    @Column(name = "trip_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '班次唯一标识（对应GTFS trip_id），如 463_0700_out'")
    private String tripId;

    @Column(name = "service_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '服务日历ID（对应GTFS service_id），引用 ServiceCalendar.serviceId'")
    private String serviceId;

    @Column(name = "direction_id", columnDefinition = "INT DEFAULT 0 COMMENT '方向ID（对应GTFS direction_id，0=正向，1=反向）'")
    @Builder.Default
    private Integer directionId = 0;

    @Column(name = "trip_headsign", columnDefinition = "VARCHAR(100) COMMENT '班车头牌文字（对应GTFS trip_headsign）'")
    private String tripHeadsign;

    @Column(name = "trip_short_name", columnDefinition = "VARCHAR(50) COMMENT '班次简称（对应GTFS trip_short_name）'")
    private String tripShortName;

    @Column(name = "block_id", columnDefinition = "VARCHAR(50) COMMENT '车辆组ID（对应GTFS block_id），同一车辆连续执行多个班次'")
    private String blockId;

    @Column(name = "shape_id", columnDefinition = "VARCHAR(50) COMMENT '形状ID（对应GTFS shape_id），引用 Shape.shapeId'")
    private String shapeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wheelchair_accessible", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '无障碍设施（对应GTFS wheelchair_accessible）'")
    @Builder.Default
    private WheelchairAccessibility wheelchairAccessible = WheelchairAccessibility.NO_INFO;

    @Enumerated(EnumType.STRING)
    @Column(name = "bikes_allowed", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '是否允许携带自行车（对应GTFS bikes_allowed）'")
    @Builder.Default
    private BikeAllowance bikesAllowed = BikeAllowance.NO_INFO;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StopTime> stopTimes = new ArrayList<>();
}