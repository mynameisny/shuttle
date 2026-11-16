package me.ningyu.app.shuttle.model.fleet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.shuttle.enums.ReportSource;
import me.ningyu.app.shuttle.enums.StopEventType;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 站点到发上报请求（手动 / 设备共用）。
 *
 * <p>一次上报只对应「到站」或「发车」其一，由 {@link #eventType} 指定；
 * 站点可由 {@link #stopId} 或 {@link #stopSequence} 任一指定（至少给一个，
 * 给 stopId 时由服务端按班次反查站序）。
 */
@Getter
@Setter
public class ReportStopEventRequest
{
    /**
     * 班次 trip_id（必填）。
     */
    @NotBlank(message = "班次不能为空")
    private String tripId;

    /**
     * 运营日期（必填）。
     */
    @NotNull(message = "运营日期不能为空")
    private LocalDate eventDate;

    /**
     * 站点 stop_id（与 stopSequence 至少给一个）。
     */
    private String stopId;

    /**
     * 站序（与 stopId 至少给一个）。
     */
    private Integer stopSequence;

    /**
     * 事件类型：到站 / 发车（必填）。
     */
    @NotNull(message = "事件类型不能为空")
    private StopEventType eventType;

    /**
     * 实际到 / 发时刻（必填）。
     */
    @NotNull(message = "实际时刻不能为空")
    private LocalTime actualTime;

    /**
     * 车牌号（可选，设备上报时可按车牌关联车辆）。
     */
    private String vehiclePlate;

    /**
     * 上报来源（可选，默认手动）。
     */
    private ReportSource source;

    /**
     * 上报人标识 / 设备号（可选）。
     */
    private String reportedBy;

    /**
     * 备注（可选）。
     */
    private String remark;
}
