package me.ningyu.app.locator.domain.route.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.common.enums.RouteStatus;
import me.ningyu.app.locator.domain.Variable;
import me.ningyu.app.locator.domain.map.entity.Station;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locator_route")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Route extends Variable
{
    /**
     * 线路编号
     */
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '线路编号'")
    private String code;

    /**
     * 线路名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '线路名称'")
    private String name;

    /**
     * 线路描述
     */
    @Column(name = "description", columnDefinition = "TEXT DEFAULT '' COMMENT '线路描述'")
    private String description;

    /**
     * 代表线路颜色（十六进制）
     */
    @Column(name = "color_Hex", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '代表线路颜色（十六进制）'")
    private String colorHex;

    /**
     * 线路起点
     */
    @Column(name = "origin", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '线路起点'")
    private String origin;

    /**
     * 线路终点
     */
    @Column(name = "terminal", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '线路终点'")
    private String terminal;

    /**
     * 线路状态
     */
    @Enumerated(value = EnumType.STRING)
    private RouteStatus status;

    /**
     * 组成线路的站点
     */
    @OneToMany
    private List<Station> stations;
}

