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
    @Column(name = "name", columnDefinition = "varchar(100) default '' comment '站点名称'")
    private String name;

    @Column(name = "address", columnDefinition = "varchar(100) default '' comment '站点地址'")
    private String address;

    @Column(name = "description", columnDefinition = "varchar(100) default '' comment '站点描述'")
    private String description;

    @Column(name = "latitude", columnDefinition = "float(11) default '' comment '纬度'")
    private Float latitude;

    @Column(name = "longitude", columnDefinition = "float(11) default '' comment '经度'")
    private Float longitude;

    @Column(name = "zip_code", columnDefinition = "varchar(100) default '' comment '邮政编码'")
    private String zipCode;

    @Column(name = "status", columnDefinition = "varchar(100) default '' comment '站点状态'")
    private StationStatus status;

    @ElementCollection
    @CollectionTable(name = "locator_station_image")
    private List<String> imageURL;
}
