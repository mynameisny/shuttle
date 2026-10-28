package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * <pre>
 * 站内楼层（对应 GTFS levels.txt）
 * 描述多层站点的楼层信息，例如：地铁站的地面层、地下一层、地下二层
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#levelstxt">levels.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - level_id    (Unique ID)  [必填] 楼层标识。
 * - level_index (Float)      [必填] 楼层索引，地面层为0，地上为正，地下为负。
 * - level_name  (Text)       [可选] 楼层名称。
 * </pre>
 */
@Entity(name = "level")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Level extends AbstractAuditable
{
    @Column(name = "level_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '楼层标识'")
    private String levelId;

    @Column(name = "level_name", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '楼层名称，如地面层、B1层'")
    private String levelName;

    @Column(name = "level_index", nullable = false, columnDefinition = "DECIMAL(5,2) COMMENT '楼层索引，地面层为0，地上为正，地下为负'")
    private BigDecimal levelIndex;
}
