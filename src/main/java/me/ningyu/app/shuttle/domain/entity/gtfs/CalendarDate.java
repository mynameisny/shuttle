package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.ExceptionType;

import java.time.LocalDate;

/**
 * <pre>
 * 日历特殊日期（对应 GTFS calendar_dates.txt）
 * 用于处理节假日、临时加开/停运等例外情况
 *
 * 参考：https://gtfs.org/documentation/schedule/reference/#calendar_datestxt
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - service_id     (Foreign ID) [必填] 服务标识。
 * - date           (Date)       [必填] 例外日期。
 * - exception_type (Enum)       [必填] 例外类型（1=加开,2=停运）。
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
