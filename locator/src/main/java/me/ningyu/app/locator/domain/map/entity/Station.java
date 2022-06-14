package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.common.enums.StationStatus;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locator_station")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Station extends Variable
{
    /**
     * 站点名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 站点地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 站点描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 纬度
     */
    @Column(name = "latitude")
    private Float latitude;

    /**
     * 经度
     */
    @Column(name = "longitude")
    private Float longitude;

    /**
     * 邮政编码
     */
    @Column(name = "zip_code")
    private String zipCode;

    /**
     * 站点状态
     */
    @Column(name = "status")
    private StationStatus status;

    @ElementCollection
    @CollectionTable(name = "locator_station_image")
    private List<String> imageURL;
}
