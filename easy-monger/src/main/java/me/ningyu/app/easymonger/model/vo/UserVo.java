package me.ningyu.app.easymonger.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * VO for {@link me.ningyu.app.easymonger.domain.auth.User}
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVo extends BaseVo
{
    private String id;
    
    private String code;
    
    private String name;
    
    private Gender gender;
    
    private String mobile;
    
    private String email;
    
    private List<RoleVo> roles;
}