package me.ningyu.app.locator.entity.user.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.entity.Variable;
import me.ningyu.app.locator.entity.user.entity.UserType;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "locator_user")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User extends Variable
{
    private String code;

    /**
     * 用户锁定.
     */
    private Boolean locked;

    /**
     * 用户类型
     */
    @Column(name = "utype")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * 组织机构名称.
     */
    private String organization;

    /**
     * 座机号
     */
    private String telephone;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;
}
