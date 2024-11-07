package me.ningyu.app.easymonger.domain.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.enums.PlatformEnum;

import java.util.List;

/**
 * 电商平台账号
 */
@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity
{
    /**
     * 账号属于哪个用户
     */
    @ManyToOne
    private User user;

    /**
     * 账号属于哪个平台，如：美团、点评、抖音
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '账号所属平台，如：美团、点评、抖音'")
    private PlatformEnum platform;

    /**
     * 手机号码
     */
    @Column(name = "mobile", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '手机号码'", nullable = false)
    private String mobile;

    /**
     * 电子邮箱
     */
    @Column(name = "email", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '电子邮箱'", nullable = false)
    private String email;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;

    /**
     * 券
     */
    // @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    // private List<Coupon> coupons;

    /**
     * 解析模板
     */
    // @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    // private List<ParseTemplate> parseTemplates;
}
