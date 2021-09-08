package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "vehicle")
@Entity
@Data
public class Vehicle
{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
     * 这辆车当前的驾驶员
     */
    @OneToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
