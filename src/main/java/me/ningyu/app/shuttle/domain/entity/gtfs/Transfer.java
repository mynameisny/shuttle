package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.TransferType;

/**
 * <pre>
 * 换乘规则（对应 GTFS transfers.txt）
 * 定义站点间的换乘关系
 *
 * transfer_type:
 * - RECOMMENDED(0): 推荐换乘点
 * - TIMED(1): 定时换乘，车辆会等待
 * - MINIMUM_TIME(2): 需要最小换乘时间
 * - NOT_POSSIBLE(3): 不可换乘
 * </pre>
 */
@Entity(name = "transfer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '起点站ID'")
    private Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '终点站ID'")
    private Stop toStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_route_id", columnDefinition = "BIGINT COMMENT '起点线路方向ID（可选）'")
    private Route fromRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_route_id", columnDefinition = "BIGINT COMMENT '终点线路方向ID（可选）'")
    private Route toRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_trip_id", columnDefinition = "BIGINT COMMENT '起点班次ID（可选）'")
    private Trip fromTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_trip_id", columnDefinition = "BIGINT COMMENT '终点班次ID（可选）'")
    private Trip toTrip;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '换乘类型'")
    private TransferType transferType;

    @Column(name = "min_transfer_time", columnDefinition = "INT COMMENT '最小换乘时间(秒)'")
    private Integer minTransferTime;
}
