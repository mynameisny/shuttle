package me.ningyu.app.easymonger.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.model.enums.AccountPlatformEnum;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable
{
    /**
     * 账号属于哪个用户
     */
    private String userId;

    /**
     * 账号属于哪个平台，如：美团、点评、抖音
     */
    private AccountPlatformEnum platform;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 备注（预留字段）
     */
    private String remark;
}