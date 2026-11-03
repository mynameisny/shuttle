package me.ningyu.app.shuttle.model.route;

import lombok.*;

import java.math.BigDecimal;
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
