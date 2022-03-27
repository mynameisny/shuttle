package me.ningyu.app.locator.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.locator.domain.user.entity.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto
{
    private UserType userType;

    private String code;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户邮箱
     */
    private String email;

    private String mobile;
}
