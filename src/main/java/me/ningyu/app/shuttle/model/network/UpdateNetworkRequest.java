package me.ningyu.app.shuttle.model.network;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNetworkRequest
{
    private String networkName;
}
