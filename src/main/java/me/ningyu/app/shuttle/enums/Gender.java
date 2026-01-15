package me.ningyu.app.shuttle.enums;

import lombok.Getter;

@Getter
public enum Gender
{
    MALE("男"), FEMALE("女");

    private final String displayName;

    Gender(String displayName)
    {
        this.displayName = displayName;
    }

    public static Gender fromName(String name)
    {
        for (Gender value : values())
        {
            if (value.displayName.equalsIgnoreCase(name))
            {
                return value;
            }
        }
        throw new RuntimeException("映射枚举失败");
    }
}
