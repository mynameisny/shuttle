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
 * 车长 / 班车管理员（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载班车运营中的车长台账：姓名、手机号。与 {@link Driver}（司机）职责区分：
 * 司机负责驾驶，车长负责跟车管理。
 */
@Entity(name = "captain")
@Table(name = "captain")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Captain extends AbstractAuditable
{
    /**
     * 车长姓名。
     */
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '车长姓名'")
    private String name;

    /**
     * 车长手机号。
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确（应为 11 位手机号）")
    @Column(name = "phone", columnDefinition = "VARCHAR(20) COMMENT '车长手机号'")
    private String phone;

    /**
     * 性别（男 / 女）。
     */
    @Column(name = "gender", columnDefinition = "VARCHAR(10) COMMENT '性别'")
    private String gender;
}
