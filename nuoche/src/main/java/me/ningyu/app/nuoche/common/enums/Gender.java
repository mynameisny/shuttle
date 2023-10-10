package me.ningyu.app.nuoche.common.enums;

public enum Gender
{
    MALE("男"),

    FEMALE("女"),

    OTHER("不方便透露");

    private final String name;

    Gender(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
