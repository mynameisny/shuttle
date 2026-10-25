package me.ningyu.app.shuttle.model.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripRequest
{
    @NotNull(message = "线路ID不能为空")
    private Long routeId;

    @NotBlank(message = "班次ID不能为空")
    private String tripId;

    @NotBlank(message = "服务日历ID不能为空")
    private String serviceId;

    @Builder.Default
    private Integer directionId = 0;

    private String tripHeadsign;

    private String tripShortName;

    private String blockId;

    private String shapeId;

    private Integer wheelchairAccessible;

    private Integer bikesAllowed;
}