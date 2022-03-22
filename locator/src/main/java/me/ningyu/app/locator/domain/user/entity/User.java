package me.ningyu.app.locator.domain.user.entity;

import lombok.Data;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class User extends Variable
{
    /**
     * 用户编号
     */
    private String code;

    /**
     * 用户名称
     */
    private String name;

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
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;
}
