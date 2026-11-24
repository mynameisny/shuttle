package me.ningyu.app.shuttle.model.fleet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 班次代班视图（按司机维度展示）。
 *
 * <p>司机管理页体现代班记录用：一条记录 = 某天某班次（精确到去/返）由某司机代班。
 * 相比直接序列化 {@code TripSubstitution} 实体，本视图裁剪掉 trip.stopTimes 等重关联，
 * 并预取线路名/颜色、代班司机名、原因中文，前端无需二次拼装。
 */
@Getter
@Setter
@Builder
public class SubstitutionView
{
    /** 代班记录主键 */
    private Long id;

    /** 代班日期 */
    private LocalDate substituteDate;

    /** 代班原因（中文：请假/有事/其他） */
    private String reason;

    /** 代班司机主键 */
    private Long driverId;

    /** 代班司机姓名 */
    private String driverName;

    /** 被代班班次 trip_id */
    private String tripId;

    /** 行驶方向（0=去程，1=返程） */
    private Integer directionId;

    /** 被代班次所属线路 route_id */
    private String routeId;

    /** 线路简称 */
    private String routeShortName;

    /** 线路全称 */
    private String routeLongName;

    /** 线路颜色（无 # 前缀） */
    private String routeColor;
}
