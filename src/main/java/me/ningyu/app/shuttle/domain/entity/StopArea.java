package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 站点区域（用户理解的站名）
 * 例如："地铁朱辛庄站"、"北京西站"、"腾讯滨海大厦"
 *
 * StopArea 不包含经纬度，因为一个站名可能对应多个实际停靠位置（Stop）
 * 例如：地铁朱辛庄站 → 东侧站台 + 西侧站台
 * </pre>
 */
@Entity(name = "stop_area")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopArea extends AbstractAuditable
{
    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '站点名称'")
    private String name;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '站点编码'")
    private String code;

    @Column(columnDefinition = "VARCHAR(50) COMMENT '所在城市'")
    private String city;

    @Column(columnDefinition = "TEXT COMMENT '备注'")
    private String remark;

    @OneToMany(mappedBy = "stopArea", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Stop> stops = new ArrayList<>();
}
