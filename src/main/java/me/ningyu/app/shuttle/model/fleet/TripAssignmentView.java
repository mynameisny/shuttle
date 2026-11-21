package me.ningyu.app.shuttle.model.fleet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 线路分配视图（线路 → 车辆/司机/车长，含去/返班次），供列表与编辑展示。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripAssignmentView
{
    private Long id;
    private String routeId;
    private String routeLabel;
    private String routeColor;
    private String outboundTripId;
    private String inboundTripId;
    private Long vehicleId;
    private String vehiclePlate;
    private String vehicleType;
    private Integer seatCount;
    private String vehiclePhotos;
    private Long driverId;
    private String driverName;
    private String driverGender;
    private String driverPhone;
    private String driverAvatar;
    private Long captainId;
    private String captainName;
    private String captainGender;
    private String captainPhone;
    private String captainAvatar;
}
