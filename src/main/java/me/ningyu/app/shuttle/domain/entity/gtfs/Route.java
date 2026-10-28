package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
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
    @JoinColumn(name = "agency_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属机构ID（对应GTFS agency_id）'")
    private Agency agency;
    
    @Column(name = "route_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '线路唯一标识（对应GTFS route_id），如 463'")
    private String routeId;

    @Column(name = "route_short_name", columnDefinition = "VARCHAR(255) COMMENT '线路简名（对应 GTFS route_short_name），如 463'")
    private String routeShortName;

    @Column(name = "route_long_name", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '线路名称（对应 GTFS route_long_name），如 463路'")
    private String routeLongName;

    @Column(name = "route_desc", columnDefinition = "TEXT COMMENT '线路描述（对应 GTFS route_desc）'")
    private String routeDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '线路类型（对应 GTFS route_type）'")
    private RouteType routeType;

    @Column(name = "route_url", columnDefinition = "VARCHAR(255) COMMENT '线路 URL（对应 GTFS route_url）'")
    private String routeUrl;

    @Column(name = "route_color", columnDefinition = "CHAR(6) COMMENT '线路颜色（对应 GTFS route_color）'")
    private String routeColor;

    @Column(name = "route_text_color", columnDefinition = "CHAR(6) COMMENT '文字颜色（对应 GTFS route_text_color）'")
    private String routeTextColor;

    @Column(name = "route_sort_order", columnDefinition = "INT COMMENT '排序（对应 GTFS route_sort_order）'")
    private Integer routeSortOrder;

    @Column(name = "continuous_pickup", columnDefinition = "INT COMMENT '连续上车（对应 GTFS continuous_pickup）'")
    private Integer continuousPickup;

    @Column(name = "continuous_drop_off", columnDefinition = "INT COMMENT '连续下车（对应 GTFS continuous_drop_off）'")
    private Integer continuousDropOff;

    @Column(name = "network_id", columnDefinition = "VARCHAR(50) COMMENT '网络 ID（对应 GTFS network_id，用于组织多条线路成一个网络）'")
    private String networkId;

    /**
     * 是否支持非接触式 EMV 支付。
     * 0 - 无信息（默认）
     * 1 - 支持
     * 2 - 不支持
     */
    @Column(name = "cemv_support", columnDefinition = "TINYINT COMMENT 'EMV支付支持（对应 GTFS cemv_support）'")
    private Integer cemvSupport;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Trip> trips = new ArrayList<>();
}
