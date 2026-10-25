package me.ningyu.app.shuttle.model.route;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ningyu.app.shuttle.enums.RouteType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRouteRequest
{
    @NotNull(message = "机构ID不能为空")
    private Long agencyId;

    @NotBlank(message = "线路ID不能为空")
    private String routeId;

    @NotBlank(message = "线路名称不能为空")
    private String routeLongName;

    private String routeShortName;

    private String routeDesc;

    @NotNull(message = "线路类型不能为空")
    private RouteType routeType;

    private String routeUrl;

    private String routeColor;

    private String routeTextColor;

    private Integer routeSortOrder;

    private Integer continuousPickup;

    private Integer continuousDropOff;

    private String networkId;
}