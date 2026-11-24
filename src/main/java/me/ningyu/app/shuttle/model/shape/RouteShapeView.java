package me.ningyu.app.shuttle.model.shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 线路描图总览视图：某条线路在某方向下的形状点列 + 线路颜色。
 *
 * <p>供「线路总览地图」页使用：大地图上一并画出多条线路的轨迹，线色取自线路 route_color。
 * 仅包含已描图（trip.shape_id 非空且 shape 表有对应点列）的线路。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteShapeView
{
    private Long routePk;
    private String routeGtfsId;
    private String routeShortName;
    private String routeLongName;
    /** 线路颜色（GTFS route_color，形如 E31C1C，前端自行加 # 前缀）。 */
    private String routeColor;
    private Integer directionId;
    private String tripId;
    /** 班次简称（trips.trip_short_name，如 霍营2早班），供总览弹层标题展示。 */
    private String tripShortName;
    /** 车头方向牌（trips.trip_headsign，如 昌平办公区），供总览弹层标题展示。 */
    private String tripHeadsign;
    private String shapeId;
    /** 班次停站数（stop_times 行数），供线路总览弹层展示。 */
    private Integer stopCount;
    private List<PointView> points;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointView
    {
        private BigDecimal lat;
        private BigDecimal lng;
    }
}
