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
    @Column(name = "pathway_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '通道标识'")
    private String pathwayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", referencedColumnName = "stop_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '通道起点位置'")
    private Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", referencedColumnName = "stop_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '通道终点位置'")
    private Stop toStop;

    @Enumerated(EnumType.STRING)
    @Column(name = "pathway_mode", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '通道类型：WALKWAY/STAIRS/MOVING_SIDEWALK/ESCALATOR/ELEVATOR/FARE_GATE/EXIT_GATE'")
    private PathwayMode pathwayMode;

    @Column(name = "is_bidirectional", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否双向通行'")
    @Builder.Default
    private Boolean isBidirectional = false;

    @Column(columnDefinition = "DECIMAL(10,2) COMMENT '通道长度(米)'")
    private BigDecimal length;

    @Column(name = "traversal_time", columnDefinition = "INT COMMENT '通过通道所需时间(秒)'")
    private Integer traversalTime;

    @Column(name = "stair_count", columnDefinition = "INT COMMENT '台阶数量'")
    private Integer stairCount;

    @Column(name = "max_slope", columnDefinition = "DECIMAL(5,2) COMMENT '最大坡度（百分比）'")
    private BigDecimal maxSlope;

    @Column(name = "min_width", columnDefinition = "DECIMAL(5,2) COMMENT '最小宽度(米)'")
    private BigDecimal minWidth;

    @Column(name = "signposted_as", columnDefinition = "VARCHAR(100) COMMENT '通道标识文字'")
    private String signpostedAs;

    @Column(name = "reversed_signposted_as", columnDefinition = "VARCHAR(100) COMMENT '反向通道标识文字'")
    private String reversedSignpostedAs;
}
