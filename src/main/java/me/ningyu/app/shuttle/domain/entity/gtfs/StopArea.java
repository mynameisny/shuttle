package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 站点-区域分配（对应 GTFS stop_areas.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#stop_areastxt">stop_areas.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 将站点分配到区域的规则，用于票价区段匹配。
 * - area_id (Foreign ID) [必填] 所属区域。
 * - stop_id (Foreign ID) [必填] 分配的站点。
 * </pre>
 */
@Entity(name = "stop_area")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopArea extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", referencedColumnName = "area_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '所属区域'")
    private Area area;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", referencedColumnName = "stop_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '分配的站点'")
    private Stop stop;
}
