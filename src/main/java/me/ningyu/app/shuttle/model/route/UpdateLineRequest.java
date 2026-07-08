package me.ningyu.app.shuttle.model.route;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLineRequest
{
    @NotBlank(message = "线路名称不能为空")
    private String name;

    private String color;

    private String description;
}
