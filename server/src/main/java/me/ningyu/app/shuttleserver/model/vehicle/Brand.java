package me.ningyu.app.shuttleserver.model.vehicle;

import lombok.Data;

import java.util.List;

/**
 * 品牌
 */
@Data
public class Brand
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
     * 正在用的Logo
     */
    private String logo;

    /**
     * 厂商
     */
    private List<Vendor> vendorList;
}
