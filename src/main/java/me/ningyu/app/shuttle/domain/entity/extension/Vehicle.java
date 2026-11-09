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

import java.time.LocalDate;

/**
 * 载具（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载班车运营中的车辆台账：车牌号、品牌/型号、座位数、车型、能源类型、
 * 车辆状态、供应商，以及保险/年检到期日等车辆自身的实用属性。
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
     * 品牌/型号（如宇通、金龙）。
     */
    @Column(name = "brand_model", columnDefinition = "VARCHAR(50) COMMENT '品牌/型号'")
    private String brandModel;

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
     * 能源类型（燃油 / 纯电 / 混动）。
     */
    @Column(name = "energy_type", columnDefinition = "VARCHAR(20) COMMENT '能源类型（燃油/纯电/混动）'")
    private String energyType;

    /**
     * 车辆状态（运营中 / 维修 / 停运）。
     */
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT '运营中' COMMENT '车辆状态（运营中/维修/停运）'")
    @Builder.Default
    private String status = "运营中";

    /**
     * 供应商（如新朗途 / 驰锐）。
     */
    @Column(name = "supplier", columnDefinition = "VARCHAR(50) COMMENT '供应商'")
    private String supplier;

    /**
     * 保险到期日。
     */
    @Column(name = "insurance_expiry", columnDefinition = "DATE COMMENT '保险到期日'")
    private LocalDate insuranceExpiry;

    /**
     * 年检到期日。
     */
    @Column(name = "inspection_expiry", columnDefinition = "DATE COMMENT '年检到期日'")
    private LocalDate inspectionExpiry;
}
