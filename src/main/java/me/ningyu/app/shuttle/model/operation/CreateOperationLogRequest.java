package me.ningyu.app.shuttle.model.operation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private Long tripId;

    @NotNull(message = "停靠点ID不能为空")
    private Long stopId;

    @NotNull(message = "实际到达时间不能为空")
    private LocalDateTime actualArrivalTime;

    private LocalDateTime actualDepartureTime;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String source;

    private String remark;
}
