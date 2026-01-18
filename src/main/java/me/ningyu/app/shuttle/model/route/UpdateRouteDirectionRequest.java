package me.ningyu.app.shuttle.model.route;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ningyu.app.shuttle.enums.Direction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRouteDirectionRequest
{
    @NotBlank(message = "线路编码不能为空")
    private String code;

    @NotNull(message = "方向不能为空")
    private Direction direction;

    private String description;

    @NotNull(message = "线路ID不能为空")
    private Long routeId;
}
