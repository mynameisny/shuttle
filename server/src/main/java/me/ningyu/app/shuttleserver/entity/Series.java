package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 车系
 */
@Table(name = "series")
@Entity
@Data
public class Series
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private SeriesType seriesType;

    /**
     * 简介
     */
    private String brief;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    /**
     * 车型
     */
    @OneToMany(mappedBy = "series")
    private List<Model> modelList;
}
