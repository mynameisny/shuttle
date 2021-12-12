package me.ningyu.app.locator.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "locator_device")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Device extends Variable
{
    /**
     * 设备厂商
     */
    private String vendor;

    /**
     * 设备型号
     */
    private String model;
}
