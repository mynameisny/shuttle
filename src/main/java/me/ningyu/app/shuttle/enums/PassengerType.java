package me.ningyu.app.shuttle.enums;

/**
 * 乘客类型枚举
 */
public enum PassengerType
{
    /**
     * 正式员工
     */
    REGULAR("正式"),

    /**
     * 派遣员工
     */
    DISPATCHED("派遣"),

    /**
     * 借调员工
     */
    SECONDED("借调");

    private final String description;

    PassengerType(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
