package me.ningyu.app.shuttle.model.network;

import lombok.*;

import java.util.List;

/**
 * 线路的多标签视图（GTFS route_networks.txt 一对多）。
 * 一条线路可同时属于多个网络，例如：昌平园区（目的地） + 北向（出发方位）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteNetworkTagsView
{
    private Long routePk;
    private String routeGtfsId;
    private String routeShortName;
    private String routeLongName;
    /** 列表场景附带的分组说明（非标签查询时为 null） */
    private String routeDesc;
    /** 列表场景附带的展示序号（非标签查询时为 null） */
    private Integer routeSortOrder;
    /** 线路背景色（6位16进制，无值时前端按默认灰处理） */
    private String routeColor;
    /** 线路文字色（6位16进制） */
    private String routeTextColor;
    private List<Tag> tags;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag
    {
        private String networkId;
        private String networkName;
    }
}
