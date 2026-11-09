package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 司机（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载班车运营中的司机台账：姓名与手机号。车辆通过 {@link Vehicle#getDriver()}
 * 关联司机（每辆车一个在册司机）。
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
    @Column(name = "phone", columnDefinition = "VARCHAR(20) COMMENT '司机手机号'")
    private String phone;
}
