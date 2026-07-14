package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * <pre>
 * 线路形状点（对应 GTFS shapes.txt）
 * 描述线路的地理轨迹，由一系列有序坐标点组成
 *
 * 一个 Route 可关联多个 Shape 点，按 sequence 排序形成完整轨迹
 * </pre>
 */
@Entity(name = "shape")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shape extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属线路方向ID'")
    private Route route;

    @Column(name = "shape_pt_lat", nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '形状点纬度'")
    private BigDecimal shapePtLat;

    @Column(name = "shape_pt_lon", nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '形状点经度'")
    private BigDecimal shapePtLon;

    @Column(name = "shape_pt_sequence", nullable = false, columnDefinition = "INT COMMENT '形状点顺序'")
    private Integer shapePtSequence;

    @Column(name = "shape_dist_traveled", columnDefinition = "DECIMAL(12,2) COMMENT '距起点的距离(米)'")
    private BigDecimal shapeDistTraveled;
}
