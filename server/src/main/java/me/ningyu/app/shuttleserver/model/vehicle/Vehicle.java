package me.ningyu.app.shuttleserver.model.vehicle;

import lombok.Data;

/**
 * 载具
 */
@Data
public abstract class Vehicle
{
    private String id;

    private Model model;

    /**
     * 车牌号
     */
    private String licensePlateNumber;
}
