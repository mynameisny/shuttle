package me.ningyu.app.easymonger.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.config.ValidGender;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAddDto implements Serializable
{
    @NotEmpty(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{4,15}$", message = "用户名只能是5~15位以内的字母、数字、下划线和中划线，且数字不能开头")
    private String code;
    
    @NotEmpty(message = "姓名不能为空")
    @Pattern(regexp = "^[a-zA-Z\\u4e00-\\u9fa5][a-zA-Z\\u4e00-\\u9fa5\\d_-]*${4,15}$", message = "姓名只能是5~15位以内的字母、数字、中文、下划线和中划线，且数字不能开头")
    private String name;
    
    @ValidGender(message = "性别只能是MALE或FEMALE或OTHER")
    private Gender gender;
    
    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[^a-zA-Z0-9]).{8,30}", message = "密码中必须包含大小写 字母、数字、特称字符，至少8个字符，最多30个字符")
    private String password;
    
    @NotEmpty(message = "手机号码不能为空")
    @Pattern(regexp = "^1([34578])\\d{9}$", message = "手机号码只能是11位以下的字母或数字")
    private String mobile;
    
    @Email
    private String email;
    
    private String remark;
}