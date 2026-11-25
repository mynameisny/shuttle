package me.ningyu.app.shuttle.event.change;

import lombok.Getter;

/**
 * 字段变更策略：控制实体字段变更是否进入变更事件及如何展示。
 */
@Getter
public enum FieldPolicyType
{
    /** 纳入事件，原样展示。 */
    INCLUDE("纳入"),

    /** 纳入事件，脱敏打码展示。 */
    MASK("打码"),

    /** 不进入事件（敏感字段排除）。 */
    EXCLUDE("排除");

    private final String name;

    FieldPolicyType(String name)
    {
        this.name = name;
    }
}
