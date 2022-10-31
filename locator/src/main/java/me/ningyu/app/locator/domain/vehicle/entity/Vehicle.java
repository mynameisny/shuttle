package me.ningyu.app.locator.domain.vehicle.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;


@Entity
@Table(name = "locator_vehicle")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends Variable
{
    /**
     * 车牌号
     */
    @Column(name = "license_plate_number", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '车牌号'")
    private String licensePlateNumber;

    /**
     * 座位数（包括司机）
     */
    @Column(name = "seats", nullable = false, columnDefinition = "INT(11) DEFAULT 1 COMMENT '座位数（包括司机）'")
    private int seats;

    /**
     * 品牌
     */
    @ManyToOne
    private Model model;
}
