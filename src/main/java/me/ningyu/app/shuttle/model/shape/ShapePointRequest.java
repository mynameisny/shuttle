package me.ningyu.app.shuttle.model.shape;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * 形状点请求（管理端地图页描线的单个顶点）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShapePointRequest
{
    @NotNull(message = "纬度不能为空")
    @DecimalMin(value = "-90", message = "纬度范围 [-90, 90]")
    @DecimalMax(value = "90", message = "纬度范围 [-90, 90]")
    private BigDecimal lat;

    @NotNull(message = "经度不能为空")
    @DecimalMin(value = "-180", message = "经度范围 [-180, 180]")
    @DecimalMax(value = "180", message = "经度范围 [-180, 180]")
    private BigDecimal lng;
}
