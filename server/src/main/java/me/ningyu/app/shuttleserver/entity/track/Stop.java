package me.ningyu.app.shuttleserver.entity.track;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;


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

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @Temporal(value = TemporalType.DATE)
    private Date arrivalTime;
}
