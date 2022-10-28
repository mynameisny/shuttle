package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "locator_address")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Address extends Variable
{
    private String name;
}
