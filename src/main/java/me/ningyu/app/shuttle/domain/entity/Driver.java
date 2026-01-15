package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.Gender;

/**
 * <pre>
 * 司机
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
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '姓名'")
    private String name;

    @Column(name = "gender", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '性别'")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "id_card", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '身份证号'")
    private String idCard;

    @Column(name = "phone", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '联系电话'")
    private String phone;

    @Column(name = "email", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '电子邮箱'")
    private String email;

    @Column(name = "company", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '所属公司'")
    private String company;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "license_id", nullable = false, columnDefinition = "BIGINT COMMENT '驾照ID'")
    @JsonManagedReference
    private DrivingLicense license;

    // @OneToOne(mappedBy = "driver")
    // private ShuttleSchedule currentSchedule;
}
