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
     * 车型
     */
    private String model;

    /**
     * 车牌号
     */
    private String licensePlateNumber;

    /**
     * 座位数（不包括司机）
     */
    private int seats;

    /**
     * 品牌
     */
    @ManyToOne
    private Brand brand;
}
