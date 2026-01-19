package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.Gender;

import java.time.LocalTime;

@Entity(name = "driving_license")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLicense extends AbstractAuditable
{
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '姓名'")
    private String name;

    @Column(name = "gender", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '性别'")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "nationality", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '国籍'")
    private String nationality;

    @Column(columnDefinition = "TEXT COMMENT '住址'")
    private String address;

    @Column(name = "license_type", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '准驾车型，如“A1、B2、C1E”'")
    private String licenseType;

    @Column(name = "license_number", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '驾照号'")
    private String licenseNumber;

    @Column(name = "date_of_birth", columnDefinition = "DATE COMMENT '出生日期'")
    private LocalTime birthDate;

    @Column(name = "date_of_first_issue", columnDefinition = "DATE COMMENT '初次领证时间'")
    private LocalTime dateOfFirstIssue;

    @Column(name = "issue_time", columnDefinition = "DATETIME COMMENT '生效时间'")
    private LocalTime issueTime;

    @Column(name = "expire_time", columnDefinition = "DATETIME COMMENT '到期时间'")
    private LocalTime expireTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Driver driver;
}
