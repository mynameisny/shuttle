package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "media_resource")
@Entity
@Data
public class MediaResource
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
