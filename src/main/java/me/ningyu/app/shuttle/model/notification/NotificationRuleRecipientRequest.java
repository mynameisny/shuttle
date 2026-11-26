package me.ningyu.app.shuttle.model.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 通知规则接收人配置请求。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRuleRecipientRequest
{
    /** 接收人解析器类型，如 TRIP_DRIVER / TRIP_CONDUCTOR / CUSTOM_USERS。 */
    @NotBlank(message = "接收人解析器类型不能为空")
    private String resolverType;

    /** 解析器参数（如 CUSTOM_USERS 的手机号，逗号分隔）。 */
    private String customValue;
}
