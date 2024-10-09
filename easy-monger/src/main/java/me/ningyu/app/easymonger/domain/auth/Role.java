package me.ningyu.app.easymonger.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.easymonger.domain.BaseEntity;

import java.util.List;

/**
 * 角色
 */
@Entity
@Table(name = "auth_role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Role extends BaseEntity
{
    /**
     * 角色编码
     */
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '角色编码'", unique = true, nullable = false)
    private String code;
    
    /**
     * 角色名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '角色名称'", unique = true, nullable = false)
    private String name;
    
    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;
    
    /**
     * 角色上配置了哪些权限
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "auth_role_permission", joinColumns = @JoinColumn(name = "role_code", referencedColumnName = "code"), inverseJoinColumns = @JoinColumn(name = "permission_code", referencedColumnName = "code"))
    private List<Permission> permissions;
    
    /**
     * 角色被分配给哪些用户
     */
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
    
}
