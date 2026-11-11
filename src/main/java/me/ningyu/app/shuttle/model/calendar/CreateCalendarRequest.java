package me.ningyu.app.shuttle.model.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 创建服务日历请求（对应 GTFS calendar.txt）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarRequest
{
    @NotBlank(message = "service_id不能为空")
    private String serviceId;

    @Builder.Default
    private Boolean monday = false;

    @Builder.Default
    private Boolean tuesday = false;

    @Builder.Default
    private Boolean wednesday = false;

    @Builder.Default
    private Boolean thursday = false;

    @Builder.Default
    private Boolean friday = false;

    @Builder.Default
    private Boolean saturday = false;

    @Builder.Default
    private Boolean sunday = false;

    @NotNull(message = "start_date不能为空")
    private LocalDate startDate;

    @NotNull(message = "end_date不能为空")
    private LocalDate endDate;
}
