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
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '站点名称'")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "locator_station_address",
               joinColumns = @JoinColumn(name = "station_id"),
               inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private Address address;

    @Column(name = "description", columnDefinition = "TEXT COMMENT '站点描述'")
    private String description;

    @Column(name = "coordinate", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '坐标系'")
    private String coordinate;

    @Column(name = "latitude", columnDefinition = "FLOAT(11) DEFAULT 0.0 COMMENT '纬度'")
    private Float latitude;

    @Column(name = "longitude", columnDefinition = "FLOAT(11) DEFAULT 0.0 COMMENT '经度'")
    private Float longitude;

    @Column(name = "zip_code", columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '邮政编码'")
    private String zipCode;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '站点状态'")
    @Enumerated(value = EnumType.STRING)
    private StationStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "locator_station_image", joinColumns = @JoinColumn(name = "station_id"))
    @Column(name = "image_url")
    private List<String> imageURL;
}
