package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

/**
 * <pre>
 * 时间框架（对应 GTFS timeframes.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#framestxt">timeframes.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 定义随时段/星期/日期变化的票价时间框架（Fares V2），与 fare_leg_rules 关联。
 * - timeframe_group_id (ID)          [必填]     时间框架组标识；同组同时段不得重叠。
 * - start_time         (Time)        [条件必填] 起始时间（含），与 end_time 同有同无，空值视为 00:00:00。
 * - end_time           (Time)        [条件必填] 结束时间（不含），空值视为 24:00:00。
 * - service_id         (Foreign ID)  [必填]     生效的日期集合（引用 calendar 或 calendar_dates）。
 * </pre>
 */
@Entity(name = "timeframe")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Timeframe extends AbstractAuditable
{
    @Column(name = "timeframe_group_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '时间框架组标识，同组同服务不得重叠'")
    private String timeframeGroupId;
    
    @Column(name = "start_time", columnDefinition = "TIME COMMENT '起始时间（含），空值视为 00:00:00'")
    private LocalTime startTime;
    
    @Column(name = "end_time", columnDefinition = "TIME COMMENT '结束时间（不含），空值视为 24:00:00'")
    private LocalTime endTime;

    @Column(name = "service_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '生效日期集合，引用 calendar.service_id 或 calendar_dates.service_id'")
    private String serviceId;
}
