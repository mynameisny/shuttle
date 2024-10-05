package me.ningyu.app.easymonger.model.enums;

import lombok.Getter;

@Getter
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
    
}
