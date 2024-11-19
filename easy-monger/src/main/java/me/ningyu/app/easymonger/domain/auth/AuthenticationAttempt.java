package me.ningyu.app.easymonger.domain.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;


/**
 * 登陆记录
 */
@Entity
@Table(name = "authentication_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationAttempt extends BaseEntity
{
    /**
     * 客户端IP地址
     */
    @Column(name = "ip", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '客户端IP地址'", nullable = false)
    private String ip;

    /**
     * 用户编码（登陆账号）
     */
    @Column(name = "code", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '用户编码（登陆账号）'", nullable = false)
    private String code;

    /**
     * 尝试密码
     */
    @Column(name = "password", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '尝试密码'")
    private String password;

    /**
     * 成功/失败
     */
    @Column(name = "is_success", columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否登陆成功：1是，0否'")
    private boolean isSuccess;
}
