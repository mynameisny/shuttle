package me.ningyu.app.locator.domain.record.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;
import me.ningyu.app.locator.domain.device.entity.Device;
import me.ningyu.app.locator.domain.map.entity.CoordType;

import javax.persistence.*;

@Entity
@Table(name = "locator_record")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Record extends Variable
{
    private String name;
}
