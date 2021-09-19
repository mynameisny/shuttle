package me.ningyu.app.shuttleserver.entity;

import lombok.Data;
import me.ningyu.app.shuttleserver.model.vehicle.Vendor;

import javax.persistence.*;
import java.util.List;

/**
 * 品牌
 */
@Table(name = "brand")
@Entity
@Data
public class Brand
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
     * 正在用的Logo
     */
    private String logo;

    /**
     * 厂商
     */
    private List<Vendor> vendorList;
}
