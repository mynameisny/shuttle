package me.ningyu.app.shuttle.model.fleet;

import lombok.Getter;
import lombok.Setter;

/**
 * 新增线路分配请求（给某线路绑定车辆/司机/车长）。
 */
@Getter
@Setter
public class CreateAssignmentRequest
{
    private String routeId;
    private Long vehicleId;
    private Long driverId;
    private Long captainId;
}
