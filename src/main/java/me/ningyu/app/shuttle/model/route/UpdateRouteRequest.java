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
public class UpdateRouteRequest
{
    @NotBlank(message = "线路名称不能为空")
    private String name;

    @NotNull(message = "方向不能为空")
    private Direction direction;

    private String description;

    private Integer sequence;

    @NotBlank(message = "班车管理员不能为空")
    private String manager;
}
