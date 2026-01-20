package me.ningyu.app.shuttle.model.shift;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSwapRequest
{
    /**
     * 要调班的班次ID
     */
    @NotNull(message = "班次ID不能为空")
    private Long shiftId;

    /**
     * 新司机ID（调班后的司机）
     */
    @NotNull(message = "新司机ID不能为空")
    private Long newDriverId;

    /**
     * 调班原因/备注
     */
    private String reason;

    /**
     * 是否为运营管理员操作（true=直接调班，false=司机发起需要审批）
     */
    private Boolean isAdminOperation = false;
}
