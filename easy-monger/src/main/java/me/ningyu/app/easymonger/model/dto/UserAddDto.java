package me.ningyu.app.easymonger.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.io.Serializable;

/**
 * DTO for {@link me.ningyu.app.easymonger.domain.auth.User}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAddDto implements Serializable
{
    private String id;
    private String code;
    private String name;
    private Gender gender;
    private String password;
    private String mobile;
    private String email;
    private String remark;
}