package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.TripStatus;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <pre>
 * 班次（对应 GTFS trips.txt）
 * 表示某一天某一时间执行的一次 Route
 *
 * 例如：463去程 07:00 京A12345 张师傅 → 这就是一个 Trip
 *
 * Trip 是整个系统的核心，GPS、到站预测、调度都围绕 Trip 展开
 * </pre>
 */
@Entity(name = "trip")
@Table(name = "shuttle_schedule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '线路方向ID'")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, columnDefinition = "BIGINT COMMENT '车辆ID'")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, columnDefinition = "BIGINT COMMENT '司机ID'")
    private Driver driver;

    @Column(name = "service_date", nullable = false, columnDefinition = "DATE COMMENT '运营日期'")
    private LocalDate serviceDate;

    @Column(name = "departure_time", nullable = false, columnDefinition = "TIME COMMENT '发车时间'")
    private LocalTime departureTime;

    @Column(name = "arrival_time", columnDefinition = "TIME COMMENT '预计到达时间'")
    private LocalTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '班次状态'")
    @Builder.Default
    private TripStatus status = TripStatus.SCHEDULED;
}
