package me.ningyu.app.shuttle.model.route;

import lombok.*;
import me.ningyu.app.shuttle.enums.ContinuousDropType;
import me.ningyu.app.shuttle.enums.RouteType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRouteRequest
{
    private String routeLongName;

    private String routeShortName;

    private String routeDesc;

    private RouteType routeType;

    private String routeUrl;

    private String routeColor;

    private String routeTextColor;

    private Integer routeSortOrder;

    private ContinuousDropType continuousPickup;

    private ContinuousDropType continuousDropOff;

    private String networkId;
}