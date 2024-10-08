package me.ningyu.app.easymonger.domain.user;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.easymonger.domain.BaseEntity;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.util.List;

/**
 * 用户
 */
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity
{
    /**
     * 用户编码
     */
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '用户编码'", unique = true, nullable = false)
    private String code;
    
    /**
     * 姓名
     */
    @Column(name = "name", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '姓名'")
    private String name;
    
    /**
     * 性别
     */
    @Column(name = "gender", columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '性别'")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    /**
     * 密码
     */
    @Column(name = "password", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '密码'", nullable = false)
    private String password;
    
    /**
     * 手机号码
     */
    @Column(name = "mobile", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '手机号码'", nullable = false)
    private String mobile;
    
    /**
     * 电子邮箱
     */
    @Column(name = "email", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '电子邮箱'")
    private String email;
    
    /**
     * 用户头像
     */
    @Column(name = "avatar", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '用户头像Path'")
    private String avatar;
    
    /**
     * 用户是否被锁定
     */
    @Column(name = "is_locked", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '用户是否被锁定：1是，0否'")
    private boolean isLocked;
    
    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;
    
    /**
     * 角色
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_code", referencedColumnName = "code"), inverseJoinColumns = @JoinColumn(name = "role_code", referencedColumnName = "code"))
    private List<Role> roles;
    
    /**
     * 用户持有的电商平台账号
     */
    /*@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Account> accounts;*/
}
