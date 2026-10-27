package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 位置组-站点分配（对应 GTFS location_group_stops.txt）
 * 将站点分配到位置组的规则，与 StopTime.location_group_id 配合实现按需服务
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#location_group_stopstxt">location_group_stops.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - location_group_id (Foreign ID) [必填]     所属位置组。
 * - stop_id           (Foreign ID) [条件必填] 分配的站点（location_id 未定义时必填）。
 * - location_id       (Foreign ID) [条件禁止] 分配的 GeoJSON 位置区域（stop_id 已定义时禁止）。
 *
 * Java 字段映射：
 * - location_group_id → locationGroup
 * - stop_id           → stop
 * - location_id       → locationId
 * </pre>
 */
@Entity(name = "location_group_stop")
@Table(name = "location_group_stop")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationGroupStop extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_group_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属位置组ID（对应GTFS location_group_id）'")
    @JsonBackReference
    private LocationGroup locationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", columnDefinition = "BIGINT COMMENT '站点ID（对应GTFS stop_id，与location_id二选一）'")
    private Stop stop;

    /**
     * GeoJSON 位置区域标识（引用 locations.geojson 的 id）。
     * 与 stop_id 互斥。
     */
    @Column(name = "location_id", columnDefinition = "VARCHAR(50) COMMENT 'GeoJSON位置区域ID（对应GTFS location_id，与stop_id互斥）'")
    private String locationId;
}
