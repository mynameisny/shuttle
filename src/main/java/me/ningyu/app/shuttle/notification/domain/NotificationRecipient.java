package me.ningyu.app.shuttle.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 通知接收人记录（一次通知的接收人快照）。
 */
@Entity(name = "sys_notification_recipient")
@Table(name = "sys_notification_recipient")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecipient extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false, columnDefinition = "BIGINT COMMENT '通知'")
    private Notification notification;

    /** 接收人标识（手机号或业务 ID）。 */
    @Column(name = "recipient_id", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '接收人标识'")
    private String recipientId;

    /** 接收人类型（DRIVER / CAPTAIN / CUSTOM）。 */
    @Column(name = "recipient_type", length = 32, columnDefinition = "VARCHAR(32) COMMENT '接收人类型'")
    private String recipientType;

    /** 接收人姓名。 */
    @Column(name = "recipient_name", length = 64, columnDefinition = "VARCHAR(64) COMMENT '接收人姓名'")
    private String recipientName;

    /** 渠道联系方式快照（JSON）。 */
    @Column(name = "contacts", columnDefinition = "TEXT COMMENT '联系方式快照(JSON)'")
    private String contacts;
}
