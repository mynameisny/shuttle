package me.ningyu.app.shuttle.model.schedule;

import lombok.*;

/**
 * 更新停站时刻请求（管理端站点维护页内联编辑）。
 * 定位：班次 + 站序（GTFS stop_times 主键语义）。
 * 三个字段均可选，null 表示不修改该字段；时间支持 HH:MM / HH:MM:SS。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStopTimeRequest
{
    /** 到站时间（HH:MM 或 HH:MM:SS），null 表示不修改 */
    private String arrivalTime;

    /** 发车时间（HH:MM 或 HH:MM:SS），null 表示不修改 */
    private String departureTime;

    /** 时间精度：APPROXIMATE=近似，EXACT=精确，null 表示不修改 */
    private String timepoint;
}
