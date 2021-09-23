package me.ningyu.app.shuttleserver.entity.vehicle;

import lombok.Data;

import javax.persistence.*;

/**
 * 车型
 */
@Table(name = "model")
@Entity
@Data
public class Model
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 中文名称
     */
    private String chineseName;

    /**
     * 英文名称
     */
    private String englishName;

    /**
     * 简介
     */
    private String brief;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;
}
