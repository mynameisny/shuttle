package me.ningyu.app.locator.domain.vehicle.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;
import java.util.List;

/**
 * 品牌厂商
 */
@Table(name = "locator_vendor")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor extends Variable
{
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
