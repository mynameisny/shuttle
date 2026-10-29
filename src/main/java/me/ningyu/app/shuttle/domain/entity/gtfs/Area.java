package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 区域（对应 GTFS areas.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#areastxt">areas.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 位置的分区分组，用于票价区段规则。
 * - area_id   (Unique ID) [必填] 区域标识。
 * - area_name (Text)      [可选] 区域名称，面向乘客展示。
 * </pre>
 */
@Entity(name = "area")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Area extends AbstractAuditable
{
    @Column(name = "area_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '区域标识'")
    private String areaId;
    @Column(name = "area_name", columnDefinition = "VARCHAR(100) COMMENT '区域名称'")
    private String areaName;
}
