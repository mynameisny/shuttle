package me.ningyu.app.shuttle.model.fleet;

import lombok.Getter;
import lombok.Setter;

/**
 * 更新班次分配请求（修改某班次的车辆/司机/车长）。
 */
@Getter
@Setter
public class UpdateAssignmentRequest
{
    private Long vehicleId;
    private Long driverId;
    private Long captainId;
}
