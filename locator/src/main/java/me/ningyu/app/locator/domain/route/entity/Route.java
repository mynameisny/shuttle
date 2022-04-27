package me.ningyu.app.locator.domain.route.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "locator_route")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Route extends Variable
{
    private String code;

    /**
     * 线路名称
     */
    private String name;

    /**
     * 线路描述
     */
    private String description;

    /**
     * 代表线路颜色（十六进制）
     */
    private String colorHex;
}

