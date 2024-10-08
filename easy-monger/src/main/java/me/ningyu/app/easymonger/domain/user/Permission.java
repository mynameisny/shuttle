package me.ningyu.app.easymonger.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import me.ningyu.app.easymonger.domain.BaseEntity;

import java.util.Collection;
import java.util.List;

/**
 * 权限
 */
@Entity
@Table(name = "permission")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Permission extends BaseEntity
{
    /**
     * 权限编码
     */
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '权限编码'", unique = true, nullable = false)
    private String code;
    
    /**
     * 权限名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '角色名称'", unique = true)
    private String name;
    
    /**
     * 权限配置给哪些角色
     */
    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
    
}
