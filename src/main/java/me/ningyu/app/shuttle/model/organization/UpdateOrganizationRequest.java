package me.ningyu.app.shuttle.model.organization;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrganizationRequest
{
    @NotBlank(message = "组织名称不能为空")
    private String name;
}
