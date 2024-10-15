package me.ningyu.app.easymonger.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    
    private String remark;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime modifiedDate;
    
    private String createdBy;
    
    private String lastModifiedBy;
}