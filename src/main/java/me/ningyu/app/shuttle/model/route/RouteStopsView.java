package me.ningyu.app.shuttle.model.route;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * 线路站点视图（管理端站点维护页"按线路筛选"）。
 * 正向查询：线路 → 全部班次停站 → 站点，按方向分组（去重并集、方向内按最小站序排列）。
 * 同一物理站台的多个同名 stop 靠方向上下文天然消歧（如去程/返程站台）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopsView
{
    private Long routePk;
    private String routeGtfsId;
    private String routeShortName;
    private String routeLongName;
    private List<DirectionStops> directions;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DirectionStops
    {
        private Integer directionId;
        private Integer tripCount;
        /** 方向内全部班次数据库主键（单班次方向供前端拖拽重排定位班次用） */
        private List<Long> tripPks;
        /** 与 tripPks 同序的班次显示名（trip_short_name 优先，回退 trip_id），供加站时选择目标班次 */
        private List<String> tripLabels;
        /** 方向内站点并集（按最小站序排列）：用于地图打点、站点总数等聚合场景 */
        private List<StopItem> stops;
        /** 按班次展开的停站序列：一条线路多班次（如霍营-昌平 1/2/3）时，前端按此逐班次展示 */
        private List<TripStops> trips;
    }

    /**
     * 单个班次在某个方向下的停站序列。
     * 「一条线路多个班次」是常见形态（同一走向的不同发车时刻，甚至走法略有差异），
     * 只有逐班次看才能看出各自的起终点与站序，并集视图会把这些信息抹平。
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripStops
    {
        private Long tripPk;
        private String tripId;
        /** 班次显示名：trip_short_name 优先，回退 trip_id */
        private String label;
        private String headsign;
        /** 该班次的首站发车时刻，可能为空 */
        private LocalTime firstDeparture;
        /** 该班次的末站到站时刻，可能为空 */
        private LocalTime lastArrival;
        /** 按 stop_sequence 排列的该班次自有停站 */
        private List<StopItem> stops;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StopItem
    {
        private Long stopPk;
        private String stopId;
        private String stopName;
        private BigDecimal lat;
        private BigDecimal lng;
        /** 方向内首次停靠序号（同一站被多班次停靠时取最小） */
        private Integer stopSequence;
        /** 方向内引用该站的班次数 */
        private Integer tripCount;
    }
}
