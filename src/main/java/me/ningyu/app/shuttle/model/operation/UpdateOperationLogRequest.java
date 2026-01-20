package me.ningyu.app.shuttle.model.operation;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOperationLogRequest
{
    /**
     * 实际到达时间
     */
    private LocalDateTime actualArrivalTime;

    /**
     * 实际出发时间
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
     * 备注
     */
    private String remark;
}
