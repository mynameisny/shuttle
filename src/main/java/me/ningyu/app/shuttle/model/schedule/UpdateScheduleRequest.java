package me.ningyu.app.shuttle.model.schedule;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest
{
    @NotNull(message = "运营日期不能为空")
    @FutureOrPresent(message = "只能编辑未来日期的排班")
    private LocalDate operatingDate;

    @NotNull(message = "发车时间不能为空")
    private LocalTime departureTime;

    @NotNull(message = "司机ID不能为空")
    private Long driverId;

    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;
}
