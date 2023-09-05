package me.ningyu.app.nuoche.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.nuoche.common.dto.validation.UserAdd;
import me.ningyu.app.nuoche.common.dto.validation.UserUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO
{
    @NotBlank(groups = {UserAdd.class}, message = "账号不能为空")
    @Null(groups = {UserUpdate.class}, message = "账号不能变更")
    private String code;

    @NotBlank(groups = {UserAdd.class}, message = "名字不能为空")
    private String name;

    private String password;

    private Set<String> phones;

    private Set<String> emails;

}
