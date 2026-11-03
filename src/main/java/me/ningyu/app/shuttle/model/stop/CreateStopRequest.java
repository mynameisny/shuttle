package me.ningyu.app.shuttle.model.stop;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * 创建停靠点请求（管理端地图页手工选点）。
 * stop_id 不传时由服务端自动生成。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStopRequest
{
    @Size(max = 50, message = "站点ID长度不能超过50")
    private String stopId;

    @NotBlank(message = "站点名称不能为空")
    @Size(max = 100, message = "站点名称长度不能超过100")
    private String stopName;

    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度范围 [-90, 90]")
    @DecimalMax(value = "90", message = "纬度范围 [-90, 90]")
    private BigDecimal lat;

    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180", message = "经度范围 [-180, 180]")
    @DecimalMax(value = "180", message = "经度范围 [-180, 180]")
    private BigDecimal lng;
}
