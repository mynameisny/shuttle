package me.ningyu.app.shuttleserver.model.vehicle;

import lombok.Data;
import me.ningyu.app.shuttleserver.model.people.Driver;

/**
 * 载具
 */
@Data
public abstract class Vehicle
{
    private String id;
    
    /**
     * 车型
     */
    private Model model;

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
    private Driver driver;
}
