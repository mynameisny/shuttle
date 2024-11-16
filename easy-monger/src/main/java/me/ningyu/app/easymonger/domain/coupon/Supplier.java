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

/**
 * 品牌店铺
 */
@Entity
@Table(name = "supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends BaseEntity
{
    /**
     * 所属品牌
     */
    @ManyToOne
    private Brand brand;

    /**
     * 店名，如：回龙观店、天通苑店
     */
    @Column(name = "shop_name", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '店名'", nullable = false)
    private String shopName;

    /**
     * 地址
     */
    @Column(name = "address", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '地址'")
    private String address;

    /**
     * 邮政编码
     */
    @Column(name = "zip_code", columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '邮政编码'")
    private String zipCode;

    /**
     * 联络电话
     */
    @Column(name = "contact_phone", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '联络电话'")
    private String contactPhone;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;
}
