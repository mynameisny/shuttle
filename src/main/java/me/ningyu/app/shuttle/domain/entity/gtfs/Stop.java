package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.LocationType;
import me.ningyu.app.shuttle.enums.StopAccess;
import me.ningyu.app.shuttle.enums.WheelchairAccessibility;

import java.math.BigDecimal;

/**
 * <pre>
 * 停靠点（对应 GTFS stops.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#stopstxt">stops.txt</a>
 *
 * GTFS stops.txt 的 location_type 区分层级，同一张表内处理：
 * - 0 STOP           → 普通站点/站台
 * - 1 STATION        → 站厅（通过 parent_station 自引用）
 * - 2 ENTRANCE_EXIT  → 出入口
 * - 3 GENERIC_NODE   → 站内通用节点
 * - 4 BOARDING_AREA  → 上车区
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - stop_id            (Unique ID)   [必填]     位置唯一标识。
 * - stop_code          (Text)        [可选]     面向乘客的站点短码。
 * - stop_name          (Text)        [条件必填] 站点名称。
 * - tts_stop_name      (Text)        [可选]     stop_name 的文本转语音版本。
 * - stop_desc          (Text)        [可选]     站点描述，应补充而非重复名称。
 * - stop_lat           (Latitude)    [条件必填] 纬度。
 * - stop_lon           (Longitude)   [条件必填] 经度。
 * - zone_id            (ID)          [可选]     票价分区。
 * - stop_url           (URL)         [可选]     站点网页URL。
 * - location_type      (Enum)        [可选]     位置类型（0/1/2/3/4）。
 * - parent_station     (Foreign ID)  [条件必填] 父级站厅，自引用实现层级。
 * - stop_timezone      (Timezone)    [可选]     站点时区，子站点继承父站时区。
 * - wheelchair_boarding(Enum)        [可选]     无障碍乘车信息。
 * - level_id           (Foreign ID)  [可选]     所属楼层。
 * - platform_code      (Text)        [可选]     站台标识。
 * - stop_access        (Enum)        [条件禁止] 站点进入方式（0=需经入口/路径,1=可直接进入）。
 *
 * Java 字段映射：
 * - stop_id            → stopId
 * - stop_code          → stopCode
 * - stop_name          → stopName
 * - tts_stop_name      → ttsStopName
 * - stop_desc          → stopDesc
 * - stop_lat           → stopLat
 * - stop_lon           → stopLon
 * - zone_id            → zoneId
 * - stop_url           → stopUrl
 * - location_type      → locationType
 * - parent_station     → parentStation
 * - stop_timezone      → stopTimezone
 * - wheelchair_boarding → wheelchairBoarding
 * - level_id           → level
 * - platform_code      → platformCode
 * - stop_access        → stopAccess
 * </pre>
 */
@Entity(name = "stop")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stop extends AbstractAuditable
{
    @Column(name = "stop_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '位置唯一标识（对应GTFS stop_id）'")
    private String stopId;

    @Column(name = "stop_code", columnDefinition = "VARCHAR(50) COMMENT '面向乘客的站点短码（对应GTFS stop_code）'")
    private String stopCode;

    @Column(name = "stop_name", columnDefinition = "VARCHAR(100) COMMENT '站点名称（对应GTFS stop_name）'")
    private String stopName;

    @Column(name = "stop_lat", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '纬度（对应GTFS stop_lat）'")
    private BigDecimal stopLat;

    @Column(name = "stop_lon", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '经度（对应GTFS stop_lon）'")
    private BigDecimal stopLon;

    @Column(name = "zone_id", columnDefinition = "VARCHAR(50) COMMENT '票价分区（对应GTFS zone_id）'")
    private String zoneId;

    @Column(name = "stop_url", columnDefinition = "VARCHAR(255) COMMENT '站点网页URL（对应GTFS stop_url）'")
    private String stopUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", columnDefinition = "VARCHAR(20) DEFAULT 'STOP' COMMENT '位置类型（对应GTFS location_type）：STOP/STATION/ENTRANCE_EXIT等'")
    @Builder.Default
    private LocationType locationType = LocationType.STOP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_station_id", columnDefinition = "BIGINT COMMENT '所属站厅ID（对应GTFS parent_station，自引用）'")
    private Stop parentStation;

    @Enumerated(EnumType.STRING)
    @Column(name = "wheelchair_boarding", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '无障碍乘车信息（对应GTFS wheelchair_boarding）'")
    @Builder.Default
    private WheelchairAccessibility wheelchairBoarding = WheelchairAccessibility.NO_INFO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", columnDefinition = "BIGINT COMMENT '所属楼层ID（对应GTFS level_id，可选）'")
    private Level level;

    @Column(name = "platform_code", columnDefinition = "VARCHAR(50) COMMENT '站台标识（对应GTFS platform_code）'")
    private String platformCode;

    /**
     * stop_name 的文本转语音版本，用于无障碍访问。
     */
    @Column(name = "tts_stop_name", columnDefinition = "VARCHAR(100) COMMENT '文本转语音站点名称（对应GTFS tts_stop_name）'")
    private String ttsStopName;

    @Column(name = "stop_desc", columnDefinition = "TEXT COMMENT '站点描述（对应GTFS stop_desc）'")
    private String stopDesc;

    /**
     * 站点时区（IANA 时区标识，如 Asia/Shanghai）。
     * 子站点继承父车站时区，不继承此字段。
     */
    @Column(name = "stop_timezone", columnDefinition = "VARCHAR(50) COMMENT '站点时区（对应GTFS stop_timezone，IANA格式如Asia/Shanghai）'")
    private String stopTimezone;

    /**
     * 站点进入方式。
     * 0 - 不可直接从街道进入，需经入口/路径
     * 1 - 可直接从街道网络进入
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stop_access", columnDefinition = "VARCHAR(20) COMMENT '站点进入方式（对应GTFS stop_access）'")
    private StopAccess stopAccess;
}
