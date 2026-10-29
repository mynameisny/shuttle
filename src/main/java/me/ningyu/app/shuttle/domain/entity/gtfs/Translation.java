package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 翻译（对应 GTFS translations.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#translationstxt">translations.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 面向乘客的数据集值翻译。
 * - table_name    (Enum)  [条件必填] 待翻译字段所在表（agency/attributions/feed_info/levels/pathways/routes/stop_times/stops/trips）。
 * - field_name    (Enum)  [条件必填] 待翻译字段。
 * - language      (Lang)  [条件必填] 翻译语言。
 * - record_id     (ID)    [条件必填] 待翻译记录的主键 ID（按记录翻译时）。
 * - record_sub_id (ID)    [条件必填] 复合主键的第二主键（stop_times 为 stop_sequence）。
 * - field_value   (Text)  [条件必填] 待翻译的值（按值翻译时应用于所有匹配记录）。
 * - translation   (Text)  [必填]     翻译后的值。
 * </pre>
 */
@Entity(name = "translation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translation extends AbstractAuditable
{
    @Column(name = "table_name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '待翻译字段所在表'")
    private String tableName;
    @Column(name = "field_name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '待翻译字段'")
    private String fieldName;
    @Column(name = "language", nullable = false, columnDefinition = "VARCHAR(10) COMMENT '翻译语言'")
    private String language;
    @Column(name = "record_id", columnDefinition = "VARCHAR(50) COMMENT '待翻译记录主键ID'")
    private String recordId;
    @Column(name = "record_sub_id", columnDefinition = "VARCHAR(50) COMMENT '复合主键第二主键，如 stop_times.stop_sequence'")
    private String recordSubId;
    @Column(name = "field_value", columnDefinition = "VARCHAR(500) COMMENT '待翻译的值'")
    private String fieldValue;
    @Column(name = "translation", nullable = false, columnDefinition = "VARCHAR(500) COMMENT '翻译后的值'")
    private String translation;
}
