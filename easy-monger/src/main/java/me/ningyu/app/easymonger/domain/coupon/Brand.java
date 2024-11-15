package me.ningyu.app.easymonger.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;

import java.util.List;

/**
 * 品牌
 */
@Entity
@Table(name = "brand")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity
{
    /**
     * 品牌名称，如：好伦哥、比格、玫瑰花园、德川家
     */
    @Column(name = "name", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '品牌名称'", nullable = false)
    private String name;

    /**
     * 编码，如：ORIGUS、BIG_PIZZA
     */
    @Column(name = "code", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '编码'")
    private String code;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;

    /**
     * 品牌的具体店铺
     */
    // @OneToMany(mappedBy = "brand")
    // private List<Supplier> suppliers;
}
