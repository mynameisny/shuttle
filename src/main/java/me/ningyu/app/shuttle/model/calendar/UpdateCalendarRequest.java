package me.ningyu.app.shuttle.model.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 更新服务日历请求：字段可选，非 null 才更新。
 * service_id 为唯一标识（被 trips 引用），不提供修改。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarRequest
{
    private Boolean monday;

    private Boolean tuesday;

    private Boolean wednesday;

    private Boolean thursday;

    private Boolean friday;

    private Boolean saturday;

    private Boolean sunday;

    private LocalDate startDate;

    private LocalDate endDate;
}
