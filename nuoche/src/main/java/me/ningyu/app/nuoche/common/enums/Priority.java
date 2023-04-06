package me.ningyu.app.nuoche.common.enums;

public enum Priority
{
    MAX(5, "max", "也可以用Urgent替换，很长时间的强烈震动，默认的通知声音与弹出的通知"),

    HIGH(4, "high","长时间的强烈震动，默认的通知声音与弹出的通知"),

    DEFAULT(3, "default","短时间、默认的震动和声音"),

    LOW(2, "low","没有震动和声音。通知将不会明显地显示，只有通知抽屉被拉下才能看到"),

    MIN(1, "min","没有震动和声音。通知将出现在“其他通知”的折叠下面");


    private int value;
    private String  code;

    private String description;

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    Priority(int value, String code, String description)
    {
        this.value = value;
        this.code = code;
        this.description = description;
    }
}
