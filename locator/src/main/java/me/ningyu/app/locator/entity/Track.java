package me.ningyu.app.locator.entity;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;

@Table(name = "")
@Entity
@Data
public class Track
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "track")
    private List<Point> points;

    private String name;
}
