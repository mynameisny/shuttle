package me.ningyu.app.shuttle.model.passenger;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePassengerPreferenceRequest
{
    @NotNull(message = "乘客ID不能为空")
    private Long passengerId;

    @NotNull(message = "线路方向ID不能为空")
    private Long routeDirectionId;

    @NotNull(message = "站点ID不能为空")
    private Long stopId;

    private Boolean isPrimary = false;

    private String description;
}
