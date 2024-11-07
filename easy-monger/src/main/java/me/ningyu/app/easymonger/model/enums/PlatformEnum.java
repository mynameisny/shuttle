package me.ningyu.app.easymonger.model.enums;

public enum PlatformEnum
{
    MEI_TUAN("美团"),

    DIAN_PING("大众点评"),

    DOU_YIN("抖音"),

    KOU_BEI("口碑");

    private final String name;

    PlatformEnum(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
