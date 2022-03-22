package me.ningyu.app.locator.common.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto
{
    private String code;

    /**
     * 用户名称
     */
    private String name;
}
