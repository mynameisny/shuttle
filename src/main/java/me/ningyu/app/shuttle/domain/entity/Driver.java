package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.DriverStatus;
import me.ningyu.app.shuttle.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * 司机
 * 合并原 DrivingLicense 实体，驾照信息直接内嵌
 * </pre>
 */
@Entity(name = "driver")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属组织ID'")
    private Organization organization;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '姓名'")
    private String name;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '联系电话'")
    private String phone;

    @Column(name = "license_no", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '驾照号'")
    private String licenseNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '司机状态'")
    @Builder.Default
    private DriverStatus status = DriverStatus.ACTIVE;

    // ==================== 原 DrivingLicense 字段 ====================

    @Column(columnDefinition = "VARCHAR(50) COMMENT '性别'")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "license_type", columnDefinition = "VARCHAR(50) COMMENT '准驾车型，如A1/B2/C1E'")
    private String licenseType;

    @Column(name = "date_of_birth", columnDefinition = "DATE COMMENT '出生日期'")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_first_issue", columnDefinition = "DATE COMMENT '初次领证时间'")
    private LocalDate dateOfFirstIssue;

    @Column(name = "issue_time", columnDefinition = "DATETIME COMMENT '驾照生效时间'")
    private LocalDateTime issueTime;

    @Column(name = "expire_time", columnDefinition = "DATETIME COMMENT '驾照到期时间'")
    private LocalDateTime expireTime;
}
