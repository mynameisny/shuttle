package me.ningyu.app.shuttle.model.calendar;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.shuttle.enums.ExceptionType;

import java.time.LocalDate;

/**
 * 给服务日历添加例外日期（calendar_dates）的请求。
 *
 * <p>用于法定假日 / 调休 / 临时停运等对常规星期规则的覆盖：
 * exceptionType=REMOVED（停运）或 ADDED（加开）。
 */
@Getter
@Setter
public class AddCalendarDateRequest
{
    @NotNull(message = "例外日期不能为空")
    private LocalDate date;

    @NotNull(message = "例外类型不能为空")
    private ExceptionType exceptionType;
}
