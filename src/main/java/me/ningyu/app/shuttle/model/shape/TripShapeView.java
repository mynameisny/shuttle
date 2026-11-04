package me.ningyu.app.shuttle.model.shape;

import lombok.*;
import me.ningyu.app.shuttle.domain.entity.gtfs.Shape;
import me.ningyu.app.shuttle.domain.entity.gtfs.StopTime;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 班次形状视图（管理端地图页数据结构）：
 * 班次与线路基础信息 + 形状点列 + 沿途停站站点。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripShapeView
{
    private Long tripPk;
    private String tripId;
    private String tripHeadsign;
    private String tripShortName;
    private Integer directionId;

    private Long routePk;
    private String routeGtfsId;
    private String routeShortName;
    private String routeLongName;

    private String shapeId;

    private List<ShapePointView> points;
    private List<StopPointView> stops;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShapePointView
    {
        private BigDecimal lat;
        private BigDecimal lng;
        private Integer sequence;
        private BigDecimal distTraveled;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StopPointView
    {
        private Long stopPk;
        private String stopId;
        private String stopName;
        private BigDecimal lat;
        private BigDecimal lng;
        private Integer stopSequence;
    }

    public static TripShapeView of(Trip trip, List<Shape> shapes, List<StopTime> stopTimes)
    {
        List<ShapePointView> points = new ArrayList<>(shapes.size());
        for (Shape s : shapes)
        {
            points.add(ShapePointView.builder()
                    .lat(s.getShapePtLat())
                    .lng(s.getShapePtLon())
                    .sequence(s.getShapePtSequence())
                    .distTraveled(s.getShapeDistTraveled())
                    .build());
        }

        List<StopPointView> stops = new ArrayList<>(stopTimes.size());
        for (StopTime st : stopTimes)
        {
            if (st.getStop() == null)
            {
                continue; // location_group / location 型停站无实体站点，地图页跳过
            }
            stops.add(StopPointView.builder()
                    .stopPk(st.getStop().getId())
                    .stopId(st.getStop().getStopId())
                    .stopName(st.getStop().getStopName())
                    .lat(st.getStop().getStopLat())
                    .lng(st.getStop().getStopLon())
                    .stopSequence(st.getStopSequence())
                    .build());
        }

        return TripShapeView.builder()
                .tripPk(trip.getId())
                .tripId(trip.getTripId())
                .tripHeadsign(trip.getTripHeadsign())
                .tripShortName(trip.getTripShortName())
                .directionId(trip.getDirectionId())
                .routePk(trip.getRoute() != null ? trip.getRoute().getId() : null)
                .routeGtfsId(trip.getRoute() != null ? trip.getRoute().getRouteId() : null)
                .routeShortName(trip.getRoute() != null ? trip.getRoute().getRouteShortName() : null)
                .routeLongName(trip.getRoute() != null ? trip.getRoute().getRouteLongName() : null)
                .shapeId(trip.getShapeId())
                .points(points)
                .stops(stops)
                .build();
    }
}
