package me.ningyu.app.easymonger.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * VO for {@link me.ningyu.app.easymonger.domain.auth.User}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVo implements Serializable
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