package me.ningyu.app.shuttle.model.stop;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * 更新停靠点请求（管理端站点维护页）。
 * stop_id 为 GTFS 主标识符且被 stop_times 引用，不允许修改；仅更新名称与坐标。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStopRequest
{
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
