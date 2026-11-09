package me.ningyu.app.shuttle.model.fleet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 载具新增/更新请求（管理端车辆维护页）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpsertRequest
{
    @NotBlank(message = "车牌号不能为空")
    @Size(max = 20, message = "车牌号长度不能超过20")
    private String plateNumber;

    @Size(max = 50, message = "品牌/型号长度不能超过50")
    private String brandModel;

    @Positive(message = "座位数须为正整数")
    private Integer seatCount;

    @Size(max = 20, message = "车型长度不能超过20")
    private String vehicleType;

    @Size(max = 20, message = "能源类型长度不能超过20")
    private String energyType;

    @Size(max = 20, message = "车辆状态长度不能超过20")
    private String status;

    @Size(max = 50, message = "供应商长度不能超过50")
    private String supplier;

    private LocalDate insuranceExpiry;

    private LocalDate inspectionExpiry;
}
