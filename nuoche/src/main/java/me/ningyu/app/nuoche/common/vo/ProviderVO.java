package me.ningyu.app.nuoche.common.vo;

import lombok.*;
import me.ningyu.app.nuoche.common.enums.ProviderVendor;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderVO
{
    private String id;

    private ProviderVendor vendor;

    private String url;

    private String pushKey;

    private String description;

    private int weight;

    private boolean enabled;
}
