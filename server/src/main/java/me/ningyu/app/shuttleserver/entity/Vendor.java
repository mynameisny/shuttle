package me.ningyu.app.shuttleserver.entity;

import lombok.Data;
import me.ningyu.app.shuttleserver.model.vehicle.Series;

import javax.persistence.*;
import java.util.List;

/**
 * 品牌厂商
 */
@Table(name = "vendor")
@Entity
@Data
public class Vendor
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 简介
     */
    private String brief;

    /**
     * 车系
     */
    private List<Series> seriesList;
}
