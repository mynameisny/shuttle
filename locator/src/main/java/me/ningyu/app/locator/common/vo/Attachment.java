package me.ningyu.app.locator.common.vo;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;

@Entity
@Table(name = "locator_attachement")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Attachment extends Variable
{
    /**
     * 附件类型
     */
    @Column(name = "type", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '附件类型'")
    private String type;

    /**
     * 附件名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(150) DEFAULT '' COMMENT '附件名称'")
    private String name;

    /**
     * 附件大小（Byte）
     */
    @Column(name = "size", columnDefinition = "BIGINT(11) DEFAULT 0 COMMENT '附件大小'")
    private long size;
}
