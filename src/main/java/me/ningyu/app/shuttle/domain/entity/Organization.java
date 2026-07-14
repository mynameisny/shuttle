package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 组织（多租户，对应GTFS的agency.txt）
 * 用于支持多组织 SaaS 模式
 * 例如：某公交公司、某企业、某学校、某园区
 * </pre>
 */
@Entity(name = "organization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends AbstractAuditable
{
    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '组织名称'")
    private String name;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '组织编码'")
    private String code;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '组织URL'")
    private String url;

    @Column(columnDefinition = "VARCHAR(50) COMMENT '时区，如Asia/Shanghai'")
    private String timezone;

    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'zh' COMMENT '语言，如zh、en'")
    @Builder.Default
    private String lang = "zh";

    @Column(columnDefinition = "VARCHAR(50) COMMENT '联系电话'")
    private String phone;

    @Column(columnDefinition = "VARCHAR(100) COMMENT '联系邮箱'")
    private String email;

    @Column(name = "fare_url", columnDefinition = "VARCHAR(255) COMMENT '票价信息网址'")
    private String fareUrl;
}
