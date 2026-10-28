package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * <pre>
 * 站内楼层（对应 GTFS levels.txt）
 * 描述多层站点的楼层信息
 *
 * 例如：地铁站的地面层、地下一层、地下二层
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
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '楼层编码'")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '楼层名称，如地面层、B1层'")
    private String name;

    @Column(name = "level_index", nullable = false, columnDefinition = "DECIMAL(5,2) COMMENT '楼层索引，地面层为0，地上为正，地下为负'")
    private BigDecimal levelIndex;
}
