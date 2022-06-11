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
    private String name;

    /**
     * 站点地址
     */
    private String address;

    /**
     * 站点描述
     */
    private String description;

    /**
     * 纬度
     */
    private Float latitude;

    /**
     * 经度
     */
    private Float longitude;

    /**
     * 邮政编码
     */
    private String zipCode;

    /**
     * 站点状态
     */
    private StationStatus status;

    @ElementCollection
    @CollectionTable(name = "locator_image")
    private List<String> imageURL;
}
