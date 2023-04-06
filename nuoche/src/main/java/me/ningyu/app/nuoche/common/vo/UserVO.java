package me.ningyu.app.nuoche.common.vo;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVO
{
    private String code;

    private String name;

    private String key;

    private Set<String> phones;

    private Set<String> emails;

    private Set<ProviderVO> providers;

}
