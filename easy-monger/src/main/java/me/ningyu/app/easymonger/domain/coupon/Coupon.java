package me.ningyu.app.easymonger.domain.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;
import me.ningyu.app.easymonger.model.enums.CouponStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物券/优惠券
 */
@Entity
@Table(name = "coupon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity
{
    /**
     * 券属于哪个账号
     */
    @ManyToOne
    private Account account;

    /**
     * 券属于哪个品牌
     */
    @Column(name = "brand", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '券属于哪个品牌'")
    private String brand;

    /**
     * 券码
     */
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '券码'", unique = true)
    private String code;

    /**
     * 券名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '券名称'")
    private String name;

    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "VARCHAR(200) DEFAULT '' COMMENT '描述'")
    private String description;

    /**
     * 订单编号
     */
    @Column(name = "order_no", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '订单编号'")
    private String orderNO;

    /**
     * 订单时间
     */
    @Column(name = "order_time", columnDefinition = "DATETIME DEFAULT NOW() COMMENT '订单时间'")
    private LocalDateTime orderTime;

    /**
     * 到期时间
     */
    @Column(name = "expire_time", columnDefinition = "DATETIME DEFAULT NOW() COMMENT '到期时间'")
    private LocalDateTime expireTime;

    /**
     * 原始价格
     */
    @Column(name = "original_price", columnDefinition = "FLOAT(7,2) DEFAULT 0.0 COMMENT '原始价格'")
    private float originalPrice;

    /**
     * 售出价格
     */
    @Column(name = "selling_price", columnDefinition = "FLOAT(7,2) DEFAULT 0.0 COMMENT '售出价格'")
    private float sellingPrice;

    /**
     * 售出时间
     */
    @Column(name = "selling_time", columnDefinition = "DATETIME DEFAULT NULL COMMENT '售出时间'")
    private LocalDateTime sellingTime;

    /**
     * 适用门店
     */
    @OneToMany(mappedBy = "brand")
    private List<Supplier> suppliers;

    /**
     * 到期自动退
     */
    @Column(name = "expired_auto_refund", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否支持到期自动退：1是，0否'")
    private boolean expiredAutoRefund;

    /**
     * 无理由随时退
     */
    @Column(name = "no_reason_refund", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否支持无理由随时退：1是，0否'")
    private boolean noReasonRefund;

    /**
     * 免预约
     */
    @Column(name = "no_appointment", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否支持免预约：1是，0否'")
    private boolean noAppointment;

    /**
     * 多店可用
     */
    @Column(name = "moreShopSupport", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '多店可用：1是，0否'")
    private boolean moreShopSupport;

    /**
     * 状态
     */
    @Column(name = "status", columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '状态'")
    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    /**
     * 图片上传路径
     */
    @OneToOne
    private Image image;

    /**
     * 标签
     */
    @Column(name = "tags", columnDefinition = "VARCHAR(200) DEFAULT '' COMMENT '标签'")
    private String tags;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;
}
