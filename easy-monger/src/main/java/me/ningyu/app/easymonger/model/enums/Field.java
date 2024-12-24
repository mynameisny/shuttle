package me.ningyu.app.easymonger.model.enums;

public enum Field
{
    NAME("名称"),
    DESCRIPTION("描述"),
    CODE("码"),
    STATUS("状态"),
    EXPIRE("过期时间"),
    ORIGINAL_PRICE("原始价格"),
    MORE_SHOP("多店使用"),
    TAGS("标签"),
    OTHER("其它");

    private final String name;

    Field(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public static Field fromName(String name)
    {
        for (Field field : Field.values())
        {
            if (field.getName().equals(name))
            {
                return field;
            }
        }
        throw new IllegalArgumentException("Unknown field: " + name);
    }
}
