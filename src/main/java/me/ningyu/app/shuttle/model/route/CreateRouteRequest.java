package me.ningyu.app.shuttle.model.route;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ningyu.app.shuttle.enums.ContinuousDropType;
import me.ningyu.app.shuttle.enums.RouteType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRouteRequest
{
    /**
     * 所属机构（可选）。机构归属由服务端隐式注入（当前单机构运营，默认 KD），
     * 前端不再让用户选择；保留此字段仅为兼容既有调用，为空时由服务端取默认机构。
     */
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

    private ContinuousDropType continuousPickup;

    private ContinuousDropType continuousDropOff;

    private String networkId;
}