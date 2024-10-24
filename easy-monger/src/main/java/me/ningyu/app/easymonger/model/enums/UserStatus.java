package me.ningyu.app.easymonger.model.enums;

import lombok.Getter;

@Getter
public enum UserStatus
{
    INACTIVE("未激活"),

    ACTIVE("已激活"),

    LOCKED("已锁定");

    private final String name;

    UserStatus(String name)
    {
        this.name = name;
    }
    
}
