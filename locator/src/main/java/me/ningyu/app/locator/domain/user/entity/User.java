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
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '用户编号'")
    private String code;

    @Column(name = "name", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '用户名字'")
    private String name;

    @Column(name = "locked", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '是否锁定'")
    private Boolean locked;

    /**
     * 用户类型
     */
    @Column(name = "utype")
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "organization", columnDefinition = "VARCHAR(150) DEFAULT '' COMMENT '组织机构名称'")
    private String organization;

    @Column(name = "organization", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '座机号'")
    private String telephone;

    @Column(name = "organization", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '手机号'")
    private String mobile;

    @Column(name = "organization", columnDefinition = "VARCHAR(150) DEFAULT '' COMMENT '邮箱'")
    private String email;

    @Column(name = "organization", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注'")
    private String remark;
}
