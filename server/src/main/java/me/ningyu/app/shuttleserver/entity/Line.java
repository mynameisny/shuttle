package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "line")
@Entity
@Data
public class Line
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 方向：去程（forward）、返程（backward）
     */
    private String direct;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToMany(mappedBy = "lines", cascade = CascadeType.ALL)
    private List<Stop> stops = new ArrayList<>();
}
