package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

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
     * 站眯描述
     */
    private String description;

    /**
     * 纬度
     */
    private Long latitude;

    /**
     * 经度
     */
    private Long longitude;

    @ElementCollection
    @CollectionTable(name = "locator_image")
    private List<String> imageURL;
}
