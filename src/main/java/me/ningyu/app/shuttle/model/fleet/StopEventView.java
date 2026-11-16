package me.ningyu.app.shuttle.model.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.enums.ReportSource;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 站点到发记录视图，供列表展示（含计划/实际时刻与偏差秒数）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopEventView
{
    private Long id;
    private String tripId;
    private String tripLabel;
    private String routeId;
    private String routeLabel;
    private Integer directionId;
    private String stopId;
    private String stopName;
    private Integer stopSequence;
    private LocalDate eventDate;
    private LocalTime scheduledArrival;
    private LocalTime scheduledDeparture;
    private LocalTime actualArrival;
    private LocalTime actualDeparture;
    private String vehiclePlate;
    private String driverName;
    private String captainName;
    private ReportSource source;
    private String sourceName;
    private String reportedBy;
    private String remark;
    /** 实际到站 - 计划到站（秒，正=晚点，负=早到，可空）。 */
    private Long arrivalDelaySec;
    /** 实际发车 - 计划发车（秒，正=晚点，负=早到，可空）。 */
    private Long departureDelaySec;
}
