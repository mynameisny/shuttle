package me.ningyu.app.easymonger.model.enums;

public enum ParserField
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

    private final String field;

    ParserField(String field)
    {
        this.field = field;
    }

    public String getField()
    {
        return field;
    }
}
