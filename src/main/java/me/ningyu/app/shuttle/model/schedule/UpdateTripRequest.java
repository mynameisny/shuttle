package me.ningyu.app.shuttle.model.schedule;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTripRequest
{
    /** 改挂线路（线路主键），null 表示不修改；停站随班次整体迁移到新线路 */
    private Long routePk;

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