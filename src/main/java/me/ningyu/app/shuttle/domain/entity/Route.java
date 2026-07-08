package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.Direction;
import me.ningyu.app.shuttle.enums.RouteStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 线路方向（对应 GTFS trips + shapes + direction 的抽象）
 * 描述一条线路的一种运行方式
 * 例如：463去程、463返程、463区间车、463节假日路线
 *
 * version 用于线路改线时的版本管理
 * </pre>
 */
@Entity(name = "route")
@Table(name = "route")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属线路ID'")
    private Line line;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '方向名称，如463去程'")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) COMMENT '方向类型'")
    private Direction direction;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1 COMMENT '版本号，用于线路改线'")
    @Builder.Default
    private Integer version = 1;

    @Column(columnDefinition = "TEXT COMMENT '线路形状（GeoJSON 或 polyline 编码）'")
    private String shape;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态'")
    @Builder.Default
    private RouteStatus status = RouteStatus.ACTIVE;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<RouteStop> stops = new ArrayList<>();
}
