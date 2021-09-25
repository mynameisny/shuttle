package me.ningyu.app.shuttleserver.entity.human;

import lombok.Data;
import me.ningyu.app.shuttleserver.entity.track.Line;

import javax.persistence.*;
import java.util.Set;

@Table(name = "passenger")
@Entity
@Data
public class Passenger extends BaseUser
{
    /**
     * 员工编号
     */
    @Column(unique = true)
    private String staffNo;
    
    /**
     * 是否为车长
     */
    private boolean isConductor;
    
    @ManyToMany
    @JoinTable(name = "rel_line_passenger",
            joinColumns = {@JoinColumn(name = "passenger_id", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "line_id", referencedColumnName = "id", nullable = false)}
    )
    private Set<Line> lines;
}
