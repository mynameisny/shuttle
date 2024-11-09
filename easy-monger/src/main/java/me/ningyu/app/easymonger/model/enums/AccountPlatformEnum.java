package me.ningyu.app.easymonger.model.enums;

public enum AccountPlatformEnum
{
    MEI_TUAN("美团"),

    DIAN_PING("大众点评"),

    DOU_YIN("抖音"),

    KOU_BEI("口碑");

    private final String name;

    AccountPlatformEnum(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
