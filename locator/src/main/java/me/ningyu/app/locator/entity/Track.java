package me.ningyu.app.locator.entity;

import lombok.Data;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Table(name = "track")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class Track extends Variable
{

    @OneToMany(mappedBy = "track")
    private List<Point> points;

    private String name;
}
