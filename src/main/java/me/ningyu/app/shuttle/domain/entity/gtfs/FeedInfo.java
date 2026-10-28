package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * <pre>
 * 数据源信息（对应 GTFS feed_info.txt）
 * 描述 GTFS 数据本身的元信息
 *
 * 每个组织可有一条 FeedInfo，记录数据发布者、版本、有效期等
 * </pre>
 */
@Entity(name = "feed_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedInfo extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属机构（业务扩展）'")
    private Agency agency;

    @Column(name = "feed_publisher_name", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '数据发布者全称'")
    private String feedPublisherName;

    @Column(name = "feed_publisher_url", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '数据发布者网址'")
    private String feedPublisherUrl;

    @Column(name = "feed_lang", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'zh' COMMENT '数据集默认语言，如 zh'")
    @Builder.Default
    private String feedLang = "zh";

    @Column(name = "default_lang", columnDefinition = "VARCHAR(10) COMMENT '多语言数据集的默认语言'")
    private String defaultLang;

    @Column(name = "feed_start_date", columnDefinition = "DATE COMMENT '数据集开始生效日期'")
    private LocalDate feedStartDate;

    @Column(name = "feed_end_date", columnDefinition = "DATE COMMENT '数据集结束生效日期'")
    private LocalDate feedEndDate;

    @Column(name = "feed_version", columnDefinition = "VARCHAR(50) COMMENT '数据版本号'")
    private String feedVersion;

    @Column(name = "feed_contact_email", columnDefinition = "VARCHAR(100) COMMENT '数据反馈邮箱'")
    private String feedContactEmail;

    @Column(name = "feed_contact_url", columnDefinition = "VARCHAR(255) COMMENT '数据反馈网址'")
    private String feedContactUrl;
}
