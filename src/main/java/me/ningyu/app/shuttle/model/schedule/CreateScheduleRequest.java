package me.ningyu.app.shuttle.model.schedule;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleRequest
{
    @NotNull(message = "线路方向ID不能为空")
    private Long routeId;

    @NotNull(message = "司机ID不能为空")
    private Long driverId;

    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;

    @NotNull(message = "开始日期不能为空")
    @FutureOrPresent(message = "开始日期不能是过去的时间")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "是否包含周末不能为空")
    @Builder.Default
    private Boolean includeWeekends = false;
}
