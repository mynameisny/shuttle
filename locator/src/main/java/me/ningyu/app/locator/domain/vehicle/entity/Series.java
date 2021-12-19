package me.ningyu.app.locator.domain.vehicle.entity;

import lombok.*;
import me.ningyu.app.locator.domain.Variable;
import me.ningyu.app.locator.enums.SeriesType;

import javax.persistence.*;
import java.util.List;

/**
 * 车系
 */
@Table(name = "locator_series")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Series extends Variable
{
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
    //@OneToMany(mappedBy = "series")
    //private List<Model> modelList;
}
