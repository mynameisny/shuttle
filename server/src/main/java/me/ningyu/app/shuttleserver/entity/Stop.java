package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 站点
 */
@Table(name = "stop")
@Entity
@Data
public class Stop
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "站点名称不能为空")
    private String name;
    
    private String description;

    private String baiduPosition;

    private String gaodePosition;

    private String googlePosition;

    @OneToMany(mappedBy = "stop")
    private List<StopResource> mediaResourceList;

    @ManyToMany
    @JoinTable(name = "rel_line_stop",
            joinColumns = {@JoinColumn(name = "stop_id", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "line_id", referencedColumnName = "id", nullable = false)}
    )
    private List<Line> lines = new ArrayList<>();

}
