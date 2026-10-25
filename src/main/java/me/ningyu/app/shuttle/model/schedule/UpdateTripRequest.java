package me.ningyu.app.shuttle.model.schedule;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTripRequest
{
    private String tripId;

    private String serviceId;

    private Integer directionId;

    private String tripHeadsign;

    private String tripShortName;

    private String blockId;

    private String shapeId;

    private Integer wheelchairAccessible;

    private Integer bikesAllowed;
}