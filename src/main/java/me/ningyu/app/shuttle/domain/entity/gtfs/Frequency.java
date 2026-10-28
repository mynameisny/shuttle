package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.ExactTimes;

import java.time.LocalTime;

/**
 * <pre>
 * 发车频率（对应 GTFS frequencies.txt）
 * 表示按固定间隔发车的班次（基于频率的服务）或相同间隔的基于时刻表的压缩表示
 *
 * 参考：https://gtfs.org/documentation/schedule/reference/#frequenciestxt
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - trip_id      (Foreign ID)      [必填]     所属班次。
 * - start_time   (Time)            [必填]     频率开始时间。
 * - end_time     (Time)            [必填]     频率结束时间。
 * - headway_secs (Positive integer)[必填]     发车间隔（秒）。
 * - exact_times  (Enum)            [可选]     是否精确时刻（0=频率制,1=时刻制）。
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
    @JoinColumn(name = "trip_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属行程'")
    private Trip trip;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIME COMMENT '频率生效开始时间'")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIME COMMENT '频率生效结束时间'")
    private LocalTime endTime;

    @Column(name = "headway_secs", nullable = false, columnDefinition = "INT COMMENT '发车间隔(秒)'")
    private Integer headwaySecs;

    @Enumerated(EnumType.STRING)
    @Column(name = "exact_times", columnDefinition = "VARCHAR(20) DEFAULT 'FREQUENCY_BASED' COMMENT '是否精确时间：FREQUENCY_BASED=基于频率，SCHEDULE_BASED=基于时刻表'")
    @Builder.Default
    private ExactTimes exactTimes = ExactTimes.FREQUENCY_BASED;
}
