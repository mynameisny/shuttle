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
public class CreateRouteRequest
{
    @NotNull(message = "线路ID不能为空")
    private Long lineId;

    @NotBlank(message = "方向名称不能为空")
    private String name;

    @NotNull(message = "方向不能为空")
    private Direction direction;

    private String shape;

    private String description;
}
