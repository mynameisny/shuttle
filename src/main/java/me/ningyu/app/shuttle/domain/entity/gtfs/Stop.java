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
 * - display_order      → displayOrder（扩展字段，非 GTFS：台账显示顺序，管理端拖拽维护）
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
    @Column(name = "stop_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '位置唯一标识，可在多条线路间复用'")
    private String stopId;

    @Column(name = "stop_code", columnDefinition = "VARCHAR(50) COMMENT '面向乘客的站点短码，常用于电话查询系统或站牌'")
    private String stopCode;

    @Column(name = "stop_name", columnDefinition = "VARCHAR(100) COMMENT '站点名称，与机构面向乘客的名称一致'")
    private String stopName;

    @Column(name = "stop_lat", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '站点纬度'")
    private BigDecimal stopLat;

    @Column(name = "stop_lon", precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '站点经度'")
    private BigDecimal stopLon;

    @Column(name = "zone_id", columnDefinition = "VARCHAR(50) COMMENT '票价区域标识'")
    private String zoneId;

    @Column(name = "stop_url", columnDefinition = "VARCHAR(255) COMMENT '站点相关网页地址'")
    private String stopUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", columnDefinition = "VARCHAR(20) DEFAULT 'STOP' COMMENT '位置类型：STOP/STATION/ENTRANCE_EXIT/GENERIC_NODE/BOARDING_AREA'")
    @Builder.Default
    private LocationType locationType = LocationType.STOP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_station", referencedColumnName = "stop_id", columnDefinition = "VARCHAR(50) COMMENT '父级车站，定义位置层级'")
    private Stop parentStation;

    @Enumerated(EnumType.STRING)
    @Column(name = "wheelchair_boarding", columnDefinition = "VARCHAR(20) DEFAULT 'NO_INFO' COMMENT '轮椅登车信息（NO_INFO/ACCESSIBLE/NOT_ACCESSIBLE）'")
    @Builder.Default
    private WheelchairAccessibility wheelchairBoarding = WheelchairAccessibility.NO_INFO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", referencedColumnName = "level_id", columnDefinition = "VARCHAR(50) COMMENT '所属楼层'")
    private Level level;

    @Column(name = "platform_code", columnDefinition = "VARCHAR(50) COMMENT '站台标识（仅标识符如 G 或 3，不含站台/轨道字样）'")
    private String platformCode;

    /**
     * stop_name 的文本转语音版本，用于无障碍访问。
     */
    @Column(name = "tts_stop_name", columnDefinition = "VARCHAR(100) COMMENT 'stop_name 的文本转语音版本'")
    private String ttsStopName;

    @Column(name = "stop_desc", columnDefinition = "TEXT COMMENT '站点描述，应补充而非重复名称'")
    private String stopDesc;

    /**
     * 站点时区（IANA 时区标识，如 Asia/Shanghai）。
     * 子站点继承父车站时区，不继承此字段。
     */
    @Column(name = "stop_timezone", columnDefinition = "VARCHAR(50) COMMENT '站点时区，IANA格式如 Asia/Shanghai'")
    private String stopTimezone;

    /**
     * 站点进入方式。
     * 0 - 不可直接从街道进入，需经入口/路径
     * 1 - 可直接从街道网络进入
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "stop_access", columnDefinition = "VARCHAR(20) COMMENT '站点进入方式（VIA_ENTRANCE=需经入口，DIRECT_ACCESS=可直接进入）'")
    private StopAccess stopAccess;

    /**
     * 台账显示顺序（扩展字段，非 GTFS stops.txt 规范字段）。
     * 管理端站点维护页拖拽排序维护；空值视为未排序排在最后，
     * 新建站点默认排最后，可拖拽调整到任意位置。
     */
    @Column(name = "display_order", columnDefinition = "INT COMMENT '台账显示顺序（管理端拖拽维护，非GTFS字段，空值排最后）'")
    private Integer displayOrder;
}
