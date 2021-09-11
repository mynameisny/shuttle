package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 路线
 */
@Table(name = "route")
@Entity
@Data
public class Route
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "路线名称不能为空")
    private String name;

    /**
     * 路线描述
     */
    private String description;
    
    /**
     * 去程站点
     */
    @OneToMany(mappedBy = "route")
    private List<Stop> forwards;
    
    /**
     * 回程站点
     */
    @OneToMany(mappedBy = "route")
    private List<Stop> backwards;
}
