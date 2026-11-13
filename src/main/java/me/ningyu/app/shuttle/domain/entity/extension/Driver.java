package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 司机（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载班车运营中的司机台账：姓名、手机号、状态、身份证号、性别、准驾车型、驾驶证编号。
 * 车辆通过 {@link Vehicle#getDriver()} 关联司机（每辆车一个在册司机）。
 */
@Entity(name = "driver")
@Table(name = "driver")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends AbstractAuditable
{
    /**
     * 司机姓名。
     */
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '司机姓名'")
    private String name;

    /**
     * 司机手机号。
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确（应为 11 位手机号）")
    @Column(name = "phone", columnDefinition = "VARCHAR(20) COMMENT '司机手机号'")
    private String phone;

    /**
     * 司机状态（在岗 / 休假 / 停用）。
     */
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT '在岗' COMMENT '司机状态（在岗/休假/停用）'")
    @Builder.Default
    private String status = "在岗";

    /**
     * 身份证号。
     */
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确（应为 18 位，末位可为 X）")
    @Column(name = "id_card_number", columnDefinition = "VARCHAR(18) COMMENT '身份证号'")
    private String idCardNumber;

    /**
     * 性别（男 / 女）。
     */
    @Column(name = "gender", columnDefinition = "VARCHAR(10) COMMENT '性别'")
    private String gender;

    /**
     * 准驾车型（如 A1、A3、C1）。
     */
    @Column(name = "license_class", columnDefinition = "VARCHAR(20) COMMENT '准驾车型'")
    private String licenseClass;

    /**
     * 驾驶证编号。
     */
    @Column(name = "license_number", columnDefinition = "VARCHAR(30) COMMENT '驾驶证编号'")
    private String licenseNumber;

    /**
     * 司机头像 URL（相对路径，如 /uploads/avatars/2026/11/xxx.jpg）。
     */
    @Column(name = "avatar_url", columnDefinition = "VARCHAR(255) COMMENT '司机头像 URL'")
    private String avatarUrl;

    /**
     * 所属公司。
     */
    @Column(name = "company", columnDefinition = "VARCHAR(100) COMMENT '所属公司'")
    private String company;
}
