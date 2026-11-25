package me.ningyu.app.shuttle.notification.recipient;

import java.util.Map;

/**
 * 统一接收人模型。
 *
 * <p>{@code contacts} 按渠道按需携带联系方式（如 SMS/INBOX → 手机号），
 * 遵循数据最小化，避免把完整个人联系方式长期复制到通知快照中。
 *
 * @param recipientId   接收人标识（本项目无用户表，暂以手机号或业务 ID 标识）
 * @param recipientType 接收人类型（DRIVER / CAPTAIN / CUSTOM）
 * @param recipientName 接收人姓名
 * @param contacts      渠道 → 联系方式映射
 */
public record Recipient(
        String recipientId,
        String recipientType,
        String recipientName,
        Map<String, String> contacts)
{
}
