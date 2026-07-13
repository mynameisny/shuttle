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
}
