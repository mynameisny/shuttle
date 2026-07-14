package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.LineStatus;
import me.ningyu.app.shuttle.enums.RouteType;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 线路（对应GTFS的routes.txt）
 * 表示一条业务线路，如：463路、企业班车A线、园区摆渡1号线
 *
 * Line 不关心具体走向，去程/返程由 Route 定义
 * </pre>
 */
@Entity(name = "line")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Line extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属组织ID'")
    private Organization organization;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '线路编码'")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '线路名称，如463路、企业班车A线'")
    private String name;

    @Column(columnDefinition = "VARCHAR(20) COMMENT '线路颜色，如FF5733'")
    private String color;

    @Column(columnDefinition = "TEXT COMMENT '线路描述'")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'BUS' COMMENT '线路类型：TRAM/SUBWAY/RAIL/BUS/FERRY等'")
    @Builder.Default
    private RouteType routeType = RouteType.BUS;

    @Column(name = "text_color", columnDefinition = "VARCHAR(20) COMMENT '文字颜色，如FFFFFF'")
    private String textColor;

    @Column(name = "sort_order", columnDefinition = "INT COMMENT '排序编号'")
    private Integer sortOrder;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '线路URL'")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '线路状态'")
    @Builder.Default
    private LineStatus status = LineStatus.ACTIVE;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Route> routes = new ArrayList<>();
}
