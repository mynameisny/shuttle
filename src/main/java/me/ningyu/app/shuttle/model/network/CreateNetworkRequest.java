package me.ningyu.app.shuttle.model.network;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNetworkRequest
{
    @NotBlank(message = "network_id不能为空")
    private String networkId;

    private String networkName;
}
