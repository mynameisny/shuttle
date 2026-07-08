package me.ningyu.app.shuttle.model.route;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLineRequest
{
    @NotNull(message = "组织ID不能为空")
    private Long organizationId;

    @NotBlank(message = "线路编码不能为空")
    private String code;

    @NotBlank(message = "线路名称不能为空")
    private String name;

    private String color;

    private String description;
}
