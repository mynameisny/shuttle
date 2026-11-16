package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import me.ningyu.app.shuttle.domain.entity.gtfs.Stop;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.enums.ReportSource;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 站点到发记录（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载「实际运行」层数据：某班次（trip）在运营日（eventDate）到达 / 离开某站点的
 * 实际时刻，与 stop_times 中的计划时刻对比可计算准点率、晚点、区间耗时。
 *
 * <p>语义上对应 GTFS-RT 的 StopTimeUpdate，但项目当前只维护静态 GTFS 计划层，
 * 实际运行数据统一收敛到本扩展实体，不触碰 gtfs 包红线。
 *
 * <p>一条记录 = 「班次 + 日期 + 站序」唯一（同一班次同一天在同一站只保留一条），
 * 实际到站 / 实际发车两个时刻由两次上报分别填充，未上报的一侧留空。
 * 上报来源可为手动（司机/车长）或车载设备，见 {@link ReportSource}。
 */
@Entity(name = "stop_event")
@Table(name = "stop_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopEvent extends AbstractAuditable
{
    /**
     * 所属班次（引用 GTFS trips.trip_id，精确到去/返）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", referencedColumnName = "trip_id", nullable = false,
            columnDefinition = "VARCHAR(50) COMMENT '班次'")
    private Trip trip;

    /**
     * 停靠站点（引用 GTFS stops.stop_id）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", referencedColumnName = "stop_id", nullable = false,
            columnDefinition = "VARCHAR(50) COMMENT '站点'")
    private Stop stop;

    /**
     * 站点在班次中的顺序（对齐 stop_times.stop_sequence）。
     */
    @Column(name = "stop_sequence", nullable = false, columnDefinition = "INT COMMENT '站序'")
    private Integer stopSequence;

    /**
     * 实际执行车辆（可空，设备/手动未提供时留空）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", columnDefinition = "BIGINT COMMENT '车辆'")
    private Vehicle vehicle;

    /**
     * 实际当班司机（代班优先，见 StopEventService）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", columnDefinition = "BIGINT COMMENT '司机'")
    private Driver driver;

    /**
     * 实际当班车长。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id", columnDefinition = "BIGINT COMMENT '车长'")
    private Captain captain;

    /**
     * 运营日期（服务日）。
     */
    @Column(name = "event_date", nullable = false, columnDefinition = "DATE COMMENT '运营日期'")
    private LocalDate eventDate;

    /**
     * 计划到站时刻（取自 stop_times.arrival_time，可空）。
     */
    @Column(name = "scheduled_arrival", columnDefinition = "TIME COMMENT '计划到站时刻'")
    private LocalTime scheduledArrivalTime;

    /**
     * 计划发车时刻（取自 stop_times.departure_time，可空）。
     */
    @Column(name = "scheduled_departure", columnDefinition = "TIME COMMENT '计划发车时刻'")
    private LocalTime scheduledDepartureTime;

    /**
     * 实际到站时刻（可空，未上报时留空）。
     */
    @Column(name = "actual_arrival", columnDefinition = "TIME COMMENT '实际到站时刻'")
    private LocalTime actualArrivalTime;

    /**
     * 实际发车时刻（可空，未上报时留空）。
     */
    @Column(name = "actual_departure", columnDefinition = "TIME COMMENT '实际发车时刻'")
    private LocalTime actualDepartureTime;

    /**
     * 上报来源（手动 / 设备）。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", columnDefinition = "VARCHAR(20) DEFAULT 'MANUAL' COMMENT '上报来源（MANUAL/DEVICE）'")
    @Builder.Default
    private ReportSource source = ReportSource.MANUAL;

    /**
     * 上报人标识（手动为司机/车长姓名，设备为设备号）。
     */
    @Column(name = "reported_by", columnDefinition = "VARCHAR(50) COMMENT '上报人/设备号'")
    private String reportedBy;

    /**
     * 备注。
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(200) COMMENT '备注'")
    private String remark;
}
