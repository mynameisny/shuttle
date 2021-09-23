package me.ningyu.app.shuttleserver.entity.pathway;

import lombok.Data;

import javax.persistence.*;

/**
 * 站点资源：图片、视频
 */
@Table(name = "stop_resource")
@Entity
@Data
public class StopResource
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String path;

    @ManyToOne
    @JoinColumn(name = "stop_id")
    private Stop stop;
}
