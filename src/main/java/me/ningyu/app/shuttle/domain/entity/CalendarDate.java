package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.ExceptionType;

import java.time.LocalDate;

/**
 * <pre>
 * 日历特殊日期（对应 GTFS calendar_dates.txt）
 * 用于处理节假日、临时加开/停运等例外情况
 *
 * exception_type:
 * - ADDED(1): 该日期加开服务
 * - REMOVED(2): 该日期停运服务
 * </pre>
 */
@Entity(name = "calendar_date")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDate extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_calendar_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属服务日历ID'")
    private ServiceCalendar serviceCalendar;

    @Column(nullable = false, columnDefinition = "DATE COMMENT '特殊日期'")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "exception_type", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '异常类型：ADDED=加开，REMOVED=停运'")
    private ExceptionType exceptionType;
}
