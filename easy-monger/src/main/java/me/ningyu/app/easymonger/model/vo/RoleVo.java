package me.ningyu.app.easymonger.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.easymonger.domain.auth.Permission;
import me.ningyu.app.easymonger.model.enums.Gender;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VO for {@link me.ningyu.app.easymonger.domain.auth.Role}
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleVo extends BaseVo
{
    private String id;
    
    private String code;
    
    private String name;
    
    private String remark;
    
    private List<PermissionVo> permissions;
}