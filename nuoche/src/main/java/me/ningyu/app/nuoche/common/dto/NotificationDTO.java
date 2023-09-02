package me.ningyu.app.nuoche.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO
{
    @NotBlank(message = "用户账号不能为空")
    private String userCode;

    private String message;

    private String userAgent;

    private String userPhone;
}
