package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 运营机构（对应 GTFS agency.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#agencytxt">agency.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - agency_id       (Unique ID)   [条件必填] 机构唯一标识。数据集包含多个机构时必填，否则推荐。
 * - agency_name     (Text)        [必填]     机构全称。
 * - agency_url      (URL)         [必填]     机构网址。
 * - agency_timezone (Timezone)    [必填]     机构所在时区（如 Asia/Shanghai）。
 * - agency_lang     (Language)    [可选]     机构主要语言（如 zh）。
 * - agency_phone    (Phone)       [可选]     语音联系电话。
 * - agency_fare_url (URL)         [可选]     票价购买或查询页面网址。
 * - agency_email    (Email)       [可选]     客服邮箱。
 * - cemv_support   (Enum)        [可选]     是否支持非接触式EMV支付（0=无信息,1=支持,2=不支持）。
 *
 * Java 字段映射：
 * - agency_id       → agencyId
 * - agency_name     → agencyName
 * - agency_url      → agencyUrl
 * - agency_timezone → agencyTimezone
 * - agency_lang     → agencyLang
 * - agency_phone    → agencyPhone
 * - agency_fare_url → agencyFareUrl
 * - agency_email    → agencyEmail
 * - cemv_support    → cemvSupport
 * </pre>
 */
@Entity(name = "agency")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agency extends AbstractAuditable
{
    @Column(name = "agency_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '机构唯一标识'")
    private String agencyId;

    @Column(name = "agency_name", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '机构名称'")
    private String agencyName;

    @Column(name = "agency_url", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '机构网址'")
    private String agencyUrl;

    @Column(name = "agency_timezone", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '时区，如 Asia/Shanghai'")
    @Builder.Default
    private String agencyTimezone = "Asia/Shanghai";

    @Column(name = "agency_lang", columnDefinition = "VARCHAR(10) DEFAULT 'zh' COMMENT '语言，如 zh、en'")
    @Builder.Default
    private String agencyLang = "zh";

    @Column(name = "agency_phone", columnDefinition = "VARCHAR(50) COMMENT '联系电话'")
    private String agencyPhone;

    @Column(name = "agency_email", columnDefinition = "VARCHAR(100) COMMENT '联系邮箱'")
    private String agencyEmail;

    @Column(name = "agency_fare_url", columnDefinition = "VARCHAR(255) COMMENT '票价网址'")
    private String agencyFareUrl;

    /**
     * 是否支持非接触式 EMV 支付。
     * 0 - 无信息（默认）
     * 1 - 支持
     * 2 - 不支持
     * 注：若同时提供 routes.cemv_support，则线路级取值优先于本字段。
     */
    @Column(name = "cemv_support", columnDefinition = "TINYINT COMMENT 'EMV支付支持（对应GTFS cemv_support）'")
    private Integer cemvSupport;
}
