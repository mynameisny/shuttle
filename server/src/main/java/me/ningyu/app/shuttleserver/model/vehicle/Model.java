package me.ningyu.app.shuttleserver.model.vehicle;

import lombok.Data;

/**
 * 车型
 */
@Data
public class Model
{
    private String id;

    /**
     * 中文名称
     */
    private String chineseName;

    /**
     * 英文名称
     */
    private String englishName;

    /**
     * 简介
     */
    private String brief;
}
