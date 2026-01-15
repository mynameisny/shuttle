package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * 排班调度
 * 某个司机在某一天的排班是开某个线路的某辆车
 * </pre>
 */
@Entity(name = "shuttle_schedule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShuttleSchedule extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, columnDefinition = "BIGINT COMMENT '司机ID'")
    private Driver driver;

    @Column(name = "operating_date", nullable = false, columnDefinition = "DATE COMMENT '运营日期，如 2026-01-15'")
    private LocalDate operatingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_direction_id", nullable = false, columnDefinition = "BIGINT COMMENT '带方向的线路ID'")
    private RouteDirection routeDirection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, columnDefinition = "BIGINT COMMENT '车辆ID'")
    private Vehicle vehicle;

    @Column(name = "departure_time", nullable = false, columnDefinition = "TIME COMMENT '发车时间'")
    private LocalTime departureTime;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否启用'")
    private Boolean active = true;
}