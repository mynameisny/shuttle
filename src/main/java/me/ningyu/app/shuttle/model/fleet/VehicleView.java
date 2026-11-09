package me.ningyu.app.shuttle.model.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;

/**
 * 载具视图：把司机信息展平（driverId/driverName/driverPhone），
 * 避免前端直接面对 Vehicle.driver 的懒加载关联。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleView
{
    private Long id;
    private String plateNumber;
    private Integer seatCount;
    private String vehicleType;
    private String supplier;
    private String adminName;
    private String adminPhone;
    private Long driverId;
    private String driverName;
    private String driverPhone;

    public static VehicleView of(Vehicle v)
    {
        Driver d = v.getDriver();
        return VehicleView.builder()
                .id(v.getId())
                .plateNumber(v.getPlateNumber())
                .seatCount(v.getSeatCount())
                .vehicleType(v.getVehicleType())
                .supplier(v.getSupplier())
                .adminName(v.getAdminName())
                .adminPhone(v.getAdminPhone())
                .driverId(d != null ? d.getId() : null)
                .driverName(d != null ? d.getName() : null)
                .driverPhone(d != null ? d.getPhone() : null)
                .build();
    }
}
