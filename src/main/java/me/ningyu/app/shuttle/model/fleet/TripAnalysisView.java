package me.ningyu.app.shuttle.model.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 班次运营分析视图（日期范围版）：某班次在一段日期内、每个「运营日」各站点的
 * 计划/实际到发明细 + 逐日准时率统计。
 *
 * <p>供乘客端「运营分析」页使用：左列按站点 × 运营日展示到发时间，右侧用柱状图展示逐日准时率。
 * 实际时刻来自 {@code stop_event}（未上报的站点实际时刻留空、不参与准时率分母）；
 * 日期范围内「非运营日」（周末/停运日）会被剔除，只返回 {@link #activeDates} 里的运营日。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripAnalysisView
{
    private String tripId;
    private String tripLabel;
    private String routeId;
    private String routeLabel;
    private Integer directionId;
    private LocalDate startDate;
    private LocalDate endDate;
    /** 日期范围内实际运营的日期（升序，已剔除停运日）。 */
    private List<LocalDate> activeDates;
    /** 各站点一行，每行含每个运营日的实际到发（按 stop_sequence 排序）。 */
    private List<StopItem> stops;
    private Summary summary;

    /**
     * 单站到发明细：计划时刻每天相同，实际到发按运营日逐日展开。
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StopItem
    {
        private Integer stopSequence;
        private String stopName;
        private LocalTime scheduledArrival;
        private LocalTime scheduledDeparture;
        /** 每个运营日一条，与 {@link TripAnalysisView#activeDates} 对齐。 */
        private List<DayData> days;
    }

    /**
     * 某站点在某个运营日的实际到发（未上报则实际值留空、偏差为空）。
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayData
    {
        private LocalDate eventDate;
        private LocalTime actualArrival;
        private LocalTime actualDeparture;
        /** 实际到站 - 计划到站（秒，正=晚点，负=早到，可空）。 */
        private Long arrivalDelaySec;
        /** 实际发车 - 计划发车（秒，正=晚点，负=早到，可空）。 */
        private Long departureDelaySec;
    }

    /**
     * 逐日准时率统计汇总。准时判定：|实际 - 计划| ≤ onTimeThresholdSec（默认 180 秒 = ±3 分钟）。
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary
    {
        private int totalStops;             // 班次停站总数（stop_times，固定）
        private int onTimeThresholdSec;     // 准时阈值（秒）
        private List<DailyPunctuality> daily; // 每个运营日一条（柱状图数据源）
    }

    /**
     * 单个运营日的准时率统计。
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPunctuality
    {
        private LocalDate eventDate;
        private int arrivalReported;        // 已上报到站数
        private int departureReported;      // 已上报发车数
        private int arrivalOnTime;          // 准时到站数
        private int departureOnTime;        // 准时发车数
        private int arrivalEarly;           // 早到站数（< -阈值）
        private int arrivalLate;            // 晚到站数（> 阈值）
        private int departureEarly;         // 早发车数
        private int departureLate;          // 晚发车数
        private double arrivalPunctuality;  // 到站准时率（%）
        private double departurePunctuality;// 发车准时率（%）
        private double overallPunctuality;  // 综合准时率（%）
    }
}
