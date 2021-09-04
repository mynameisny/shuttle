package me.ningyu.app.shuttleserver.model.vehicle;

import lombok.Data;

import java.util.List;

/**
 * 车系
 */
@Data
public class Series
{
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private SeriesType seriesType;

    /**
     * 简介
     */
    private String brief;

    /**
     * 车型
     */
    private List<Model> modelList;
}
