package me.ningyu.app.locator.domain.vehicle.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


@Entity
@Table(name = "locator_brand")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends Variable
{
    /**
     * 名称
     */
    private String name;

    /**
     * 简介
     */
    private String brief;

    /**
     * 正在用的Logo
     */
    private String logo;


}
