package me.ningyu.app.shuttle.iam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 角色（IAM 域，表名带 iam_ 前缀）。
 *
 * <p>角色是 ABAC 主体属性的一种来源：用户经角色映射（jCasbin 的 {@code g} 策略）
 * 获得 {@code code}，授权策略（{@code p} 策略）按角色编码匹配资源与动作。
 * 本表仅存角色的元数据，供管理界面展示；用户-角色映射由 jCasbin 策略承载。
 */
@Entity(name = "iam_role")
@Table(name = "iam_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamRole extends AbstractAuditable
{
    /** 角色编码（唯一，如 ADMIN / OPERATOR / VIEWER）。 */
    @Column(name = "code", nullable = false, unique = true, length = 50,
            columnDefinition = "VARCHAR(50) COMMENT '角色编码'")
    private String code;

    /** 角色名称。 */
    @Column(name = "name", nullable = false, length = 50,
            columnDefinition = "VARCHAR(50) COMMENT '角色名称'")
    private String name;

    /** 角色描述。 */
    @Column(name = "description", length = 255, columnDefinition = "VARCHAR(255) COMMENT '角色描述'")
    private String description;
}
