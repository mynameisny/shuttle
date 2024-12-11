package me.ningyu.app.easymonger.domain.coupon;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;

@Entity
@Table(name = "image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends BaseEntity
{
    /**
     * 图片原始名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '图片原始名称'")
    private String name;

    /**
     * 图片上传路径
     */
    @Column(name = "path", columnDefinition = "VARCHAR(200) DEFAULT '' COMMENT '图片上传路径'")
    private String path;

    /**
     * 图片正被使用
     */
    @Column(name = "is_used", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '图片正被使用：1是，0否'")
    private boolean inUsed;

    /**
     * 文件的校验和：用于检查文件完整性，检测文件是否被恶意篡改，以及是否重复
     */
    @Column(name = "checksum", unique = true, nullable = false, columnDefinition = "VARCHAR(128) DEFAULT '' COMMENT '文件的校验和'")
    private String checksum;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", columnDefinition = "VARCHAR(36) DEFAULT NULL COMMENT '账号ID'")
    private Account account;

    /*@ManyToOne
    private User user;*/
}
