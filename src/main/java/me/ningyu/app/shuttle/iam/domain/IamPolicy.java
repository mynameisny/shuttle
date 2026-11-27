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
 * jCasbin 授权策略行（IAM 域，表名带 iam_ 前缀）。
 *
 * <p>对应 jCasbin 标准 {@code casbin_rule} 表结构（{@code ptype} + {@code v0..v5}）：
 * <ul>
 *   <li>{@code p} 策略：{@code v0}=角色或用户、{@code v1}=资源模式（支持 keyMatch）、{@code v2}=动作；</li>
 *   <li>{@code g} 策略：{@code v0}=用户、{@code v1}=角色（用户-角色映射，即「角色作为主体属性」的载体）。</li>
 * </ul>
 * 由 jCasbin 的 Enforcer 经自定义 Adapter 加载，支持按角色（RBAC 语义）与按属性（ABAC 语义）统一决策。
 */
@Entity(name = "iam_casbin_rule")
@Table(name = "iam_casbin_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamPolicy extends AbstractAuditable
{
    /** 策略类型：p（权限策略）/ g（角色映射）。 */
    @Column(name = "ptype", nullable = false, length = 10, columnDefinition = "VARCHAR(10) COMMENT '策略类型(p/g)'")
    private String ptype;

    @Column(name = "v0", length = 100, columnDefinition = "VARCHAR(100) COMMENT '策略值0'")
    private String v0;

    @Column(name = "v1", length = 100, columnDefinition = "VARCHAR(100) COMMENT '策略值1'")
    private String v1;

    @Column(name = "v2", length = 100, columnDefinition = "VARCHAR(100) COMMENT '策略值2'")
    private String v2;

    @Column(name = "v3", length = 100, columnDefinition = "VARCHAR(100) COMMENT '策略值3'")
    private String v3;

    @Column(name = "v4", length = 100, columnDefinition = "VARCHAR(100) COMMENT '策略值4'")
    private String v4;

    @Column(name = "v5", length = 100, columnDefinition = "VARCHAR(100) COMMENT '策略值5'")
    private String v5;
}
