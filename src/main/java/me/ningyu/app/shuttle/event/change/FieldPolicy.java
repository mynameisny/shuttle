package me.ningyu.app.shuttle.event.change;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 实体字段变更策略配置。
 *
 * <p>默认（无记录）= INCLUDE，即实体任何字段变化都会进入变更事件；
 * 敏感字段可通过 EXCLUDE 排除、MASK 脱敏，运行时增删无需改代码。
 */
@Entity(name = "sys_event_field_policy")
@Table(name = "sys_event_field_policy",
        uniqueConstraints = @UniqueConstraint(name = "uk_field_policy_entity_field",
                columnNames = {"entity_type", "field_name"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldPolicy extends AbstractAuditable
{
    /** 目标实体类型（DRIVER / CAPTAIN / VEHICLE / ROUTE ...）。 */
    @Column(name = "entity_type", nullable = false, length = 32, columnDefinition = "VARCHAR(32) COMMENT '实体类型'")
    private String entityType;

    /** 字段名（技术名）。 */
    @Column(name = "field_name", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '字段名'")
    private String fieldName;

    /** 字段中文展示名。 */
    @Column(name = "field_label", length = 64, columnDefinition = "VARCHAR(64) COMMENT '字段中文名'")
    private String fieldLabel;

    /** 变更策略。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "policy", nullable = false, length = 16, columnDefinition = "VARCHAR(16) COMMENT '策略'")
    private FieldPolicyType policy;
}
