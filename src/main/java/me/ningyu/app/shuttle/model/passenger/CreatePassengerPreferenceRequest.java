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
    private Long routeId;

    @NotNull(message = "停靠点ID不能为空")
    private Long stopId;

    @Builder.Default
    private Boolean isPrimary = false;

    private String description;
}
