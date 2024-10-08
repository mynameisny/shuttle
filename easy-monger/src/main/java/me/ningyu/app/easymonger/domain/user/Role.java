package me.ningyu.app.easymonger.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import me.ningyu.app.easymonger.domain.BaseEntity;

import java.util.List;

/**
 * 角色
 */
@Entity
@Table(name = "role")
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

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    private Collection<Permission> permissions;*/
    
    /**
     * 角色分配给哪些用户
     */
    @ManyToMany(mappedBy = "roles")
    private List<User> users;
    
}
