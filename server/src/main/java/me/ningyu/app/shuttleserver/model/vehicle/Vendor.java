package me.ningyu.app.shuttleserver.model.vehicle;

import lombok.Data;

import java.util.List;

/**
 * 品牌厂商
 */
@Data
public class Vendor
{
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 简介
     */
    private String brief;

    /**
     * 车系
     */
    private List<Series> seriesList;
}
