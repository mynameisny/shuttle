package me.ningyu.app.shuttle.event.change;

/**
 * 实体字段变更记录（含策略处理后的展示值）。
 *
 * @param fieldName  字段名（技术名）
 * @param fieldLabel 字段中文展示名
 * @param oldValue   旧值（已按策略脱敏）
 * @param newValue   新值（已按策略脱敏）
 */
public record FieldChange(
        String fieldName,
        String fieldLabel,
        String oldValue,
        String newValue)
{
}
