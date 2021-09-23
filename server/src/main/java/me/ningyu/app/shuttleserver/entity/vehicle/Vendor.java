package me.ningyu.app.shuttleserver.entity.vehicle;

import lombok.Data;
import me.ningyu.app.shuttleserver.entity.vehicle.Brand;
import me.ningyu.app.shuttleserver.entity.vehicle.Series;

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
     * 所属品牌
     */
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    /**
     * 车系
     */
    @OneToMany
    @JoinColumn(name = "vendor")
    private List<Series> seriesList;
}
