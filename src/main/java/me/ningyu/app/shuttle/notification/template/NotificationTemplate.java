package me.ningyu.app.shuttle.notification.template;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import me.ningyu.app.shuttle.notification.ChannelType;

/**
 * 通知模板（标题与正文支持 {@code {{变量名}}} 占位符）。
 */
@Entity(name = "sys_notification_template")
@Table(name = "sys_notification_template",
        uniqueConstraints = @UniqueConstraint(name = "uk_notification_template_code", columnNames = "template_code"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate extends AbstractAuditable
{
    /** 模板编码（唯一）。 */
    @Column(name = "template_code", nullable = false, length = 64, columnDefinition = "VARCHAR(64) COMMENT '模板编码'")
    private String templateCode;

    /** 模板名称。 */
    @Column(name = "name", nullable = false, length = 128, columnDefinition = "VARCHAR(128) COMMENT '模板名称'")
    private String name;

    /** 标题模板（可含占位符）。 */
    @Column(name = "title", length = 255, columnDefinition = "VARCHAR(255) COMMENT '标题模板'")
    private String title;

    /** 正文模板（可含占位符）。 */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT COMMENT '正文模板'")
    private String content;

    /** 适用渠道（空 = 通用）。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 16, columnDefinition = "VARCHAR(16) COMMENT '适用渠道'")
    private ChannelType channel;
}
