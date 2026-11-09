package me.ningyu.app.shuttle.model.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;

import java.time.LocalDate;

/**
 * 载具视图：车辆台账的扁平化展示对象。
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
    private String brandModel;
    private Integer seatCount;
    private String vehicleType;
    private String energyType;
    private String status;
    private String supplier;
    private LocalDate insuranceExpiry;
    private LocalDate inspectionExpiry;

    public static VehicleView of(Vehicle v)
    {
        return VehicleView.builder()
                .id(v.getId())
                .plateNumber(v.getPlateNumber())
                .brandModel(v.getBrandModel())
                .seatCount(v.getSeatCount())
                .vehicleType(v.getVehicleType())
                .energyType(v.getEnergyType())
                .status(v.getStatus())
                .supplier(v.getSupplier())
                .insuranceExpiry(v.getInsuranceExpiry())
                .inspectionExpiry(v.getInspectionExpiry())
                .build();
    }
}
