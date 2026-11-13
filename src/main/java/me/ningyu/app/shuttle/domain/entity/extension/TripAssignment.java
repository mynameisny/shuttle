package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;

/**
 * 线路分配（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载「线路 → 车辆 + 司机 + 车长」的长期绑定关系，对应 CSV 一行
 * 「车牌 + 司机 + 班车管理员 + 路线」的绑定。一条线路有去程/返程两个班次，
 * 但车辆/司机/车长是线路级的，去返共享，故分配挂线路（route）而非班次（trip）。
 * 代班（某天某班次临时替换司机）见 {@link TripSubstitution}。
 */
@Entity(name = "trip_assignment")
@Table(name = "trip_assignment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripAssignment extends AbstractAuditable
{
    /**
     * 被分配的线路（引用 GTFS routes.route_id 原生 ID）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", nullable = false,
            columnDefinition = "VARCHAR(50) COMMENT '线路'")
    private Route route;

    /**
     * 执行线路的车辆。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, columnDefinition = "BIGINT COMMENT '车辆'")
    private Vehicle vehicle;

    /**
     * 驾驶线路的司机。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, columnDefinition = "BIGINT COMMENT '司机'")
    private Driver driver;

    /**
     * 跟线的车长 / 管理员（摆渡车等短驳线可能无车长，故可空）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id", columnDefinition = "BIGINT COMMENT '车长'")
    private Captain captain;
}
