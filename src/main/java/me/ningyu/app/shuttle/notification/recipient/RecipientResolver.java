package me.ningyu.app.shuttle.notification.recipient;

import java.util.List;

/**
 * 接收人解析器 SPI。
 *
 * <p>同一事件的接收人通常由业务关系动态决定，规则只引用解析器类型，
 * 不直接维护大量用户 ID。解析器在通知编排事务内执行，可访问 LAZY 关联。
 */
public interface RecipientResolver
{
    /** 解析器类型编码，如 TRIP_DRIVER / TRIP_CONDUCTOR / CUSTOM_USERS。 */
    String getType();

    /** 解析接收人列表。 */
    List<Recipient> resolve(RecipientContext context);
}
