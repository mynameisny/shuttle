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
    @JoinColumn(name = "from_stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '换乘起点站'")
    private Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '换乘终点站'")
    private Stop toStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_route_id", columnDefinition = "BIGINT COMMENT '限定换乘的起点线路'")
    private Route fromRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_route_id", columnDefinition = "BIGINT COMMENT '限定换乘的终点线路'")
    private Route toRoute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_trip_id", columnDefinition = "BIGINT COMMENT '限定换乘的起点行程'")
    private Trip fromTrip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_trip_id", columnDefinition = "BIGINT COMMENT '限定换乘的终点行程'")
    private Trip toTrip;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '换乘类型：RECOMMENDED/TIMED/MINIMUM_TIME/NOT_POSSIBLE'")
    private TransferType transferType;

    @Column(name = "min_transfer_time", columnDefinition = "INT COMMENT '换乘所需最短时间(秒)'")
    private Integer minTransferTime;
}
