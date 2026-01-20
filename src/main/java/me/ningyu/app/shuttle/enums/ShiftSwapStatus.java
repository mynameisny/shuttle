package me.ningyu.app.shuttle.enums;

/**
 * 班次调班状态枚举
 */
public enum ShiftSwapStatus
{
    /**
     * 正常状态（未发生调班）
     */
    NORMAL("正常"),

    /**
     * 调班申请待审批
     */
    PENDING_APPROVAL("待审批"),

    /**
     * 调班已批准
     */
    APPROVED("已批准"),

    /**
     * 调班已拒绝
     */
    REJECTED("已拒绝");

    private final String description;

    ShiftSwapStatus(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
