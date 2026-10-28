package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.PathwayMode;

import java.math.BigDecimal;

/**
 * <pre>
 * 站内通道（对应 GTFS pathways.txt）
 * 描述大型站点内部的通道连接
 *
 * 例如：地铁站内的换乘通道、楼梯、电梯、扶梯等
 * </pre>
 */
@Entity(name = "pathway")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pathway extends AbstractAuditable
{
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '通道编码'")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '起点站ID'")
    private Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '终点站ID'")
    private Stop toStop;

    @Enumerated(EnumType.STRING)
    @Column(name = "pathway_mode", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '通道类型：WALKWAY/STAIRS/ESCALATOR/ELEVATOR等'")
    private PathwayMode pathwayMode;

    @Column(name = "is_bidirectional", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否双向通行'")
    @Builder.Default
    private Boolean isBidirectional = false;

    @Column(columnDefinition = "DECIMAL(10,2) COMMENT '通道长度(米)'")
    private BigDecimal length;

    @Column(name = "traversal_time", columnDefinition = "INT COMMENT '通行时间(秒)'")
    private Integer traversalTime;

    @Column(name = "stair_count", columnDefinition = "INT COMMENT '台阶数量'")
    private Integer stairCount;

    @Column(name = "max_slope", columnDefinition = "DECIMAL(5,2) COMMENT '最大坡度'")
    private BigDecimal maxSlope;

    @Column(name = "min_width", columnDefinition = "DECIMAL(5,2) COMMENT '最小宽度(米)'")
    private BigDecimal minWidth;

    @Column(name = "signposted_as", columnDefinition = "VARCHAR(100) COMMENT '标识文字'")
    private String signpostedAs;

    @Column(name = "reversed_signposted_as", columnDefinition = "VARCHAR(100) COMMENT '反向标识文字'")
    private String reversedSignpostedAs;
}
