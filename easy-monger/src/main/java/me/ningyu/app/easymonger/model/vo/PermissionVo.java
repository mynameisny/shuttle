package me.ningyu.app.easymonger.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

/**
 * VO for {@link me.ningyu.app.easymonger.domain.auth.Permission}
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionVo extends BaseVo
{
    private String id;
    
    private String code;
    
    private String name;
    
    private String remark;
}