package me.ningyu.app.shuttle.model.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.notification.ChannelType;
import me.ningyu.app.shuttle.notification.DeliveryPolicy;
import me.ningyu.app.shuttle.notification.Severity;
import me.ningyu.app.shuttle.notification.TriggerMode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知规则创建 / 更新请求（含渠道与接收人配置，整体覆盖式提交）。
 *
 * <p>{@code conditionExpression} 为条件表达式 JSON AST 字符串（可为空表示无条件命中）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRuleRequest
{
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @NotBlank(message = "事件类型不能为空")
    private String eventType;

    private TriggerMode triggerMode;

    private Severity severity;

    private DeliveryPolicy deliveryPolicy;

    /** 条件表达式（JSON AST，可为空）。 */
    private String conditionExpression;

    private Boolean enabled;

    private Integer priority;

    private LocalDateTime effectiveStart;

    private LocalDateTime effectiveEnd;

    /** 消息模板编码（可为空）。 */
    private String templateCode;

    /** 渠道列表。 */
    private List<ChannelType> channels;

    /** 接收人配置列表。 */
    private List<NotificationRuleRecipientRequest> recipients;
}
