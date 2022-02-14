package me.ningyu.app.locator.common.vo;

import lombok.Data;

@Data
public class StationDto
{

    /**
     * 站点名称
     */
    private String name;

    /**
     * 站点地址
     */
    private String address;

    /**
     * 站点描述
     */
    private String description;

    /**
     * 纬度
     */
    private Float latitude;

    /**
     * 经度
     */
    private Float longitude;
}
