package me.ningyu.app.shuttleserver.entity.track;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 路线实体类
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
     * 线路状态
     */
    private String status;
    
    @OneToMany(mappedBy = "route")
    private Set<Line> lineList;
}
