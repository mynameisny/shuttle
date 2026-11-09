package me.ningyu.app.shuttle.model.fleet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 载具新增/更新请求（管理端司机载具维护页）。
 * 司机通过 driverId 关联（可为空，表示暂未分配司机）。
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

    @Positive(message = "座位数须为正整数")
    private Integer seatCount;

    @Size(max = 20, message = "车型长度不能超过20")
    private String vehicleType;

    @Size(max = 50, message = "供应商长度不能超过50")
    private String supplier;

    @Size(max = 50, message = "班车管理员长度不能超过50")
    private String adminName;

    @Size(max = 20, message = "管理员电话长度不能超过20")
    private String adminPhone;

    private Long driverId;
}
