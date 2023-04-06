package me.ningyu.app.nuoche.common.dto;

import lombok.*;
import me.ningyu.app.nuoche.common.dto.validation.ProviderAdd;
import me.ningyu.app.nuoche.common.enums.ProviderVendor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProviderDTO
{
    @NotNull(groups = {ProviderAdd.class}, message = "通知服务提供商不能为空")
    private ProviderVendor vendor;

    @NotBlank(groups = {ProviderAdd.class}, message = "通知服务URL不能为空")
    private String url;

    @NotBlank(groups = {ProviderAdd.class}, message = "通知服务PushKey不能为空")
    private String pushKey;

    private String description;

    private int weight;

    private Boolean enabled = true;
}
