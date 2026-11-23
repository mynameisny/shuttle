package me.ningyu.app.shuttle.event.api;

/**
 * 业务领域事件统一契约（Event Center 第一阶段）。
 *
 * <p>业务模块只依赖本接口发布领域事件，不感知后续的规则匹配、通知渠道与接收人，
 * 符合「业务与基础设施解耦」与「业务模块只发布事件」的设计原则。
 *
 * <p>实现类使用稳定字符串事件编码（见 {@code event.core.EventTypes}），
 * 不使用 Java 类全限定名；{@code data} 为业务事实快照，供消费方理解事件，
 * 不应携带个人敏感信息或完整联系方式。
 */
public interface DomainEvent
{
    /** 稳定事件编码，例如 {@code trip.cancelled}。 */
    String getEventType();

    /** 事件数据结构版本，用于后续结构演进。 */
    int getEventVersion();

    /** 业务数据快照（不要求消费方再回查可能已变化的实体）。 */
    Object getData();

    /**
     * 业务实体标识，例如 {@code trip/10001}；可选，默认空。
     * 用于填充事件信封的 {@code subject} 字段。
     */
    default String getSubject()
    {
        return null;
    }
}
