package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 载具（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载班车运营中的车辆台账：车牌号、座位数、车型、供应商，以及班车管理员信息，
 * 并通过 {@link #driver} 关联在册司机。
 */
@Entity(name = "vehicle")
@Table(name = "vehicle")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends AbstractAuditable
{
    /**
     * 车牌号（唯一）。
     */
    @Column(name = "plate_number", nullable = false, unique = true, columnDefinition = "VARCHAR(20) COMMENT '车牌号'")
    private String plateNumber;

    /**
     * 座位数。
     */
    @Column(name = "seat_count", columnDefinition = "INT COMMENT '座位数'")
    private Integer seatCount;

    /**
     * 车型（大巴 / 中巴 / 考斯特）。
     */
    @Column(name = "vehicle_type", columnDefinition = "VARCHAR(20) COMMENT '车型（大巴/中巴/考斯特）'")
    private String vehicleType;

    /**
     * 供应商（如新朗途 / 驰锐）。
     */
    @Column(name = "supplier", columnDefinition = "VARCHAR(50) COMMENT '供应商'")
    private String supplier;

    /**
     * 班车管理员姓名。
     */
    @Column(name = "admin_name", columnDefinition = "VARCHAR(50) COMMENT '班车管理员'")
    private String adminName;

    /**
     * 班车管理员电话。
     */
    @Column(name = "admin_phone", columnDefinition = "VARCHAR(20) COMMENT '管理员电话'")
    private String adminPhone;

    /**
     * 在册司机。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", columnDefinition = "BIGINT COMMENT '司机主键'")
    private Driver driver;
}
