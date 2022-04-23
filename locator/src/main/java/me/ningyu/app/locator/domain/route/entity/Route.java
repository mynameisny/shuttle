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

    private String name;

    private String description;
}

