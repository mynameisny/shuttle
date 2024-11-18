package me.ningyu.app.easymonger.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;

@Entity
@Table(name = "parse_template_coordinate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParseTemplateCoordinate extends BaseEntity
{
    /**
     * 截图坐标属于哪个解析模板
     */
    @ManyToOne
    private ParseTemplate parseTemplate;

    /**
     * 图片左上角，距0点的横向距离
     */
    @Column(name = "x", columnDefinition = "INTEGER(11) DEFAULT 0 COMMENT '图片左上角，距0点的横向距离'")
    private int x;

    /**
     * 图片左上角，距0点的纵向距离
     */
    @Column(name = "y", columnDefinition = "INTEGER(11) DEFAULT 0 COMMENT '图片左上角，距0点的纵向距离'")
    private int y;

    /**
     * 图片的宽度
     */
    @Column(name = "width", columnDefinition = "INTEGER(11) DEFAULT 0 COMMENT '图片的宽度'")
    private int width;

    /**
     * 图片的高度
     */
    @Column(name = "height", columnDefinition = "INTEGER(11) DEFAULT 0 COMMENT '图片的高度'")
    private int height;

    /**
     * 字段标识
     */
    /*@Column(name = "field", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '字段标识'")
    @Enumerated(EnumType.STRING)
    private Field field;*/

    /**
     * 是否要删除字符串中间的空格
     */
    @Column(name = "delete_space", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否要删除字符串中间的空格：1是，0否'")
    private boolean deleteSpace;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;
}
