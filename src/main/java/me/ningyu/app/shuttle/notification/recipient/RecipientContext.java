package me.ningyu.app.shuttle.notification.recipient;

import java.util.Map;

/**
 * 接收人解析上下文。
 *
 * @param eventType   事件编码
 * @param subject     业务实体标识（如 trip/10001）
 * @param data        事件数据快照（扁平 Map）
 * @param customValue 规则接收人配置的自定义参数（如 CUSTOM_USERS 的手机号）
 */
public record RecipientContext(
        String eventType,
        String subject,
        Map<String, Object> data,
        String customValue)
{
}
