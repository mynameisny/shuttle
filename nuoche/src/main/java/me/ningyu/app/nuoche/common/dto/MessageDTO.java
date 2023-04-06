package me.ningyu.app.nuoche.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO
{
    private String host;

    private int port;

    private String pushKey;

    private String title;

    private String body;

    private String level;

    private String group;

    private String url;
}
