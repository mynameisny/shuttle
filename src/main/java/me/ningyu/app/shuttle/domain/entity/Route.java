package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 线路的模板
 * 只维护线路的基础信息，如：名称、管理员
 * </pre>
 */
@Entity(name = "route")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route extends AbstractAuditable
{
    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '线路名称，如：“霍营3线”'")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '方向（上行/下行/环线）'")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(columnDefinition = "TEXT COMMENT '描述，如“回龙观 - 昌平科技园”'")
    private String description;

    @Column(name = "sequence", columnDefinition = "INT(11) COMMENT '自定义排序编号'")
    private Integer sequence = 0;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '班车管理员'")
    private String manager;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RouteDirection> directionalRoutes = new ArrayList<>();
}
