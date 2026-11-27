package me.ningyu.app.shuttle.iam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 账号用户（IAM 域，表名带 iam_ 前缀，与 GTFS 实体及 sys_ 系统表区分）。
 *
 * <p>只承载认证与授权所需的账号主体信息；业务人员（司机/车长等）与账号的关系
 * 通过 {@code username} 或后续的绑定表建立，本实体保持通用、可复用。
 */
@Entity(name = "iam_user")
@Table(name = "iam_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUser extends AbstractAuditable
{
    /** 登录用户名（唯一）。 */
    @Column(name = "username", nullable = false, unique = true, length = 50,
            columnDefinition = "VARCHAR(50) COMMENT '登录用户名'")
    private String username;

    /** 密码哈希（BCrypt），不存明文，序列化时忽略。 */
    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 100,
            columnDefinition = "VARCHAR(100) COMMENT 'BCrypt 密码哈希'")
    private String passwordHash;

    /** 显示名 / 真实姓名。 */
    @Column(name = "display_name", length = 50, columnDefinition = "VARCHAR(50) COMMENT '显示名/真实姓名'")
    private String displayName;

    /** 手机号（可用于找回 / 通知接收人标识）。 */
    @Column(name = "phone", length = 20, columnDefinition = "VARCHAR(20) COMMENT '手机号'")
    private String phone;

    /** 邮箱。 */
    @Column(name = "email", length = 100, columnDefinition = "VARCHAR(100) COMMENT '邮箱'")
    private String email;

    /** 账号状态（ACTIVE / DISABLED / LOCKED）。 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '账号状态(ACTIVE/DISABLED/LOCKED)'")
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;
}
