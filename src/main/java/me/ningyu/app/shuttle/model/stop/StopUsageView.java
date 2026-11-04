package me.ningyu.app.shuttle.model.stop;

import lombok.*;
import me.ningyu.app.shuttle.domain.entity.gtfs.Route;
import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.enums.Timepoint;

import java.time.LocalTime;

/**
 * 站点引用视图：引用某站点的班次及其所属线路（管理端站点维护页）。
 * 基于 stop_times 反查，用于展示修改站点坐标的影响面，也是将来站点删除保护的基础；
 * 附带该引用的到/发时刻与时间精度，供站点维护页内联编辑时刻。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopUsageView
{
    private Long tripPk;
    private String tripId;
    private Integer directionId;
    private Integer stopSequence;

    private Long routePk;
    private String routeGtfsId;
    private String routeShortName;
    private String routeLongName;

    private String shapeId;

    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Timepoint timepoint;

    public static StopUsageView of(StopTime st)
    {
        Trip trip = st.getTrip();
        Route route = trip != null ? trip.getRoute() : null;
        return StopUsageView.builder()
                .tripPk(trip != null ? trip.getId() : null)
                .tripId(trip != null ? trip.getTripId() : null)
                .directionId(trip != null ? trip.getDirectionId() : null)
                .stopSequence(st.getStopSequence())
                .routePk(route != null ? route.getId() : null)
                .routeGtfsId(route != null ? route.getRouteId() : null)
                .routeShortName(route != null ? route.getRouteShortName() : null)
                .routeLongName(route != null ? route.getRouteLongName() : null)
                .shapeId(trip != null ? trip.getShapeId() : null)
                .arrivalTime(st.getArrivalTime())
                .departureTime(st.getDepartureTime())
                .timepoint(st.getTimepoint())
                .build();
    }
}
