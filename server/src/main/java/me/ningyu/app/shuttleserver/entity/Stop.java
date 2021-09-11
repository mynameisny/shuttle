package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @OneToMany(mappedBy = "stop")
    private List<StopResource> mediaResourceList;

}
