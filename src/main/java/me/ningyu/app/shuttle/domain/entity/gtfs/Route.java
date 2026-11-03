package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.CemvSupport;
import me.ningyu.app.shuttle.enums.ContinuousDropType;
import me.ningyu.app.shuttle.enums.RouteType;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 线路（对应 GTFS routes.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#routestxt">routes.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - route_id            (Unique ID)           [必填]     线路唯一标识。
 * - agency_id           (Foreign ID)          [条件必填] 所属机构。多个机构时必填，否则推荐。
 * - route_short_name    (Text)                [条件必填] 线路简名（如 "32", "100X", "Green"）。
 * - route_long_name     (Text)                [条件必填] 线路全名（更描述性，常含目的地）。
 * - route_desc          (Text)                [可选]     线路描述，应补充而非重复名称。
 * - route_type          (Enum)                [必填]     交通类型（0=电车,1=地铁,2=铁路,3=公交,4=轮渡等）。
 * - route_url           (URL)                 [可选]     线路相关网页URL。
 * - route_color         (Color, 6位16进制)     [可选]     线路颜色（默认FFFFFF）。
 * - route_text_color    (Color, 6位16进制)     [可选]     线路文字颜色（默认000000）。
 * - route_sort_order    (Non-negative int)    [可选]     显示排序，值越小越靠前。
 * - continuous_pickup   (Enum)                [条件禁止] 连续上车规则（0=允许,1=不允许,2=电话预约,3=司机协调）。
 * - continuous_drop_off (Enum)                [条件禁止] 连续下车规则（同上）。
 * - network_id          (ID)                  [条件禁止] 线路网络分组ID，与route_networks.txt互斥。
 * - cemv_support        (Enum)                [可选]     是否支持非接触式EMV支付（0=无信息,1=支持,2=不支持）。
 *
 * Java 字段映射：
 * - route_id            → routeId
 * - agency_id           → agency
 * - route_short_name    → routeShortName
 * - route_long_name     → routeLongName
 * - route_desc          → routeDesc
 * - route_type          → routeType
 * - route_url           → routeUrl
 * - route_color         → routeColor
 * - route_text_color    → routeTextColor
 * - route_sort_order    → routeSortOrder
 * - continuous_pickup   → continuousPickup
 * - continuous_drop_off → continuousDropOff
 * - network_id          → networkId
 * - cemv_support        → cemvSupport
 * </pre>
 */
@Entity(name = "route")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", referencedColumnName = "agency_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '线路所属机构'")
    private Agency agency;
    
    @Column(name = "route_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '线路唯一标识，如 463'")
    private String routeId;

    @Column(name = "route_short_name", columnDefinition = "VARCHAR(255) COMMENT '线路简称，如 32、100X、Green'")
    private String routeShortName;

    @Column(name = "route_long_name", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '线路全称，更描述性，常含目的地信息'")
    private String routeLongName;

    @Column(name = "route_desc", columnDefinition = "TEXT COMMENT '线路描述，应提供有用信息，不应与名称重复'")
    private String routeDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '交通类型：TRAM/SUBWAY/RAIL/BUS/FERRY等'")
    private RouteType routeType;

    @Column(name = "route_url", columnDefinition = "VARCHAR(255) COMMENT '线路相关网页地址'")
    private String routeUrl;

    @Column(name = "route_color", columnDefinition = "CHAR(6) COMMENT '线路背景颜色（6位16进制，默认FFFFFF）'")
    private String routeColor;

    @Column(name = "route_text_color", columnDefinition = "CHAR(6) COMMENT '线路文字颜色（6位16进制，默认000000）'")
    private String routeTextColor;

    @Column(name = "route_sort_order", columnDefinition = "INT COMMENT '展示排序，值越小越靠前'")
    private Integer routeSortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "continuous_pickup", columnDefinition = "VARCHAR(20) DEFAULT 'NOT_ALLOWED' COMMENT '连续上车规则（ALLOWED/NOT_ALLOWED/PHONE_AGENCY/COORDINATE_WITH_DRIVER），可被 stop_times 覆盖'")
    @Builder.Default
    private ContinuousDropType continuousPickup = ContinuousDropType.NOT_ALLOWED;

    @Enumerated(EnumType.STRING)
    @Column(name = "continuous_drop_off", columnDefinition = "VARCHAR(20) DEFAULT 'NOT_ALLOWED' COMMENT '连续下车规则（取值同 continuous_pickup），可被 stop_times 覆盖'")
    @Builder.Default
    private ContinuousDropType continuousDropOff = ContinuousDropType.NOT_ALLOWED;

    @Column(name = "network_id", columnDefinition = "VARCHAR(50) COMMENT '线路分组标识，将多条线路组织成网络'")
    private String networkId;

    /**
     * 是否支持非接触式 EMV 支付（GTFS cemv_support）。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "cemv_support", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFORMATION' COMMENT 'EMV支付支持（NO_INFORMATION/SUPPORTED/NOT_SUPPORTED）'")
    @Builder.Default
    private CemvSupport cemvSupport = CemvSupport.NO_INFORMATION;

    /**
     * 线路下的班次集合。
     * 序列化忽略：线路列表接口不应级联携带全部班次（查班次走 GET /api/trips/by-route/{routeId}），
     * 且 Route.trips ↔ Trip.route 双向引用会造成 Jackson 无限循环。
     */
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Trip> trips = new ArrayList<>();
}
