package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.ExactTimes;

import java.time.LocalTime;

/**
 * <pre>
 * 发车频率（对应 GTFS frequencies.txt）
 * 定义高频线路的发车间隔
 *
 * 例如：07:00-09:00 每10分钟一班
 * </pre>
 */
@Entity(name = "frequency")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Frequency extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属班次ID'")
    private Trip trip;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIME COMMENT '频率开始时间'")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIME COMMENT '频率结束时间'")
    private LocalTime endTime;

    @Column(name = "headway_secs", nullable = false, columnDefinition = "INT COMMENT '发车间隔(秒)'")
    private Integer headwaySecs;

    @Enumerated(EnumType.STRING)
    @Column(name = "exact_times", columnDefinition = "VARCHAR(20) DEFAULT 'FREQUENCY_BASED' COMMENT '是否精确时间：FREQUENCY_BASED=基于频率，SCHEDULE_BASED=基于时刻表'")
    @Builder.Default
    private ExactTimes exactTimes = ExactTimes.FREQUENCY_BASED;
}
