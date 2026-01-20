package me.ningyu.app.shuttle.model.operation;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.ningyu.app.shuttle.enums.GPSPriority;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOperationLogRequest
{
    @NotNull(message = "班次ID不能为空")
    private Long shiftId;

    @NotNull(message = "站点ID不能为空")
    private Long stopId;

    @NotNull(message = "实际到达时间不能为空")
    private LocalDateTime actualArrivalTime;

    /**
     * 实际出发时间（可选，不填则默认 = 到达时间 + 1分钟）
     */
    private LocalDateTime actualDepartureTime;

    /**
     * GPS纬度
     */
    private BigDecimal latitude;

    /**
     * GPS经度
     */
    private BigDecimal longitude;

    /**
     * GPS数据优先级
     */
    private GPSPriority gpsPriority;

    /**
     * 数据来源描述
     */
    private String source;

    /**
     * 备注（如晚点原因）
     */
    private String remark;
}
