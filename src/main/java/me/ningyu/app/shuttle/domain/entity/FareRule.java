package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 票价规则（对应 GTFS fare_rules.txt）
 * 定义票价适用的线路和区域
 *
 * 一条票价规则可指定：
 * - 适用的线路（route）
 * - 起点区域（origin）
 * - 终点区域（destination）
 * - 途经区域（contains）
 * </pre>
 */
@Entity(name = "fare_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareRule extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fare_attribute_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属票价属性ID'")
    private FareAttribute fareAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", columnDefinition = "BIGINT COMMENT '适用线路ID（可选）'")
    private Line line;

    @Column(name = "origin_id", columnDefinition = "VARCHAR(50) COMMENT '起点区域编码（可选）'")
    private String originId;

    @Column(name = "destination_id", columnDefinition = "VARCHAR(50) COMMENT '终点区域编码（可选）'")
    private String destinationId;

    @Column(name = "contains_id", columnDefinition = "VARCHAR(50) COMMENT '途经区域编码（可选）'")
    private String containsId;
}
