package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Table(name = "line")
@Entity
@Data
public class Line
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private LineDirect direct;
    
    /**
     * 车长
     */
    @ManyToMany(mappedBy = "lines")
    private Set<Passenger> conductors;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToMany(mappedBy = "lines", cascade = CascadeType.ALL)
    private List<Stop> stops = new ArrayList<>();
}
