package me.ningyu.app.locator.common.vo;

import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;

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
    private Long latitude;

    /**
     * 经度
     */
    private Long longitude;
}
