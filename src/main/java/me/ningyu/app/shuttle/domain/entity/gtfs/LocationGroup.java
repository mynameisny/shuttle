package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 位置组（对应 GTFS location_groups.txt）
 * 一组站点，共同表示乘客可请求上车或下车的位置（GTFS-Flex 按需服务）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#location_groupstxt">location_groups.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - location_group_id   (Unique ID) [必填] 位置组唯一标识。
 *                        在 stops.stop_id、locations.geojson id、location_groups.location_group_id 之间须全局唯一。
 * - location_group_name (Text)       [可选] 位置组名称，面向乘客展示。
 *
 * Java 字段映射：
 * - location_group_id   → locationGroupId
 * - location_group_name → locationGroupName
 * </pre>
 */
@Entity(name = "location_group")
@Table(name = "location_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationGroup extends AbstractAuditable
{
    @Column(name = "location_group_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '位置组标识，须在所有位置标识中全局唯一'")
    private String locationGroupId;

    @Column(name = "location_group_name", columnDefinition = "VARCHAR(100) COMMENT '位置组名称，面向乘客展示'")
    private String locationGroupName;
}
