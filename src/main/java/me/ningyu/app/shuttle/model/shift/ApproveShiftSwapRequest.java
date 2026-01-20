package me.ningyu.app.shuttle.model.shift;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveShiftSwapRequest
{
    /**
     * 班次ID
     */
    @NotNull(message = "班次ID不能为空")
    private Long shiftId;

    /**
     * 是否批准（true=批准，false=拒绝）
     */
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    /**
     * 审批人ID
     * TODO: 创建 User 实体后改为从认证上下文获取
     */
    private Long approverId;

    /**
     * 审批备注
     */
    private String remark;
}
