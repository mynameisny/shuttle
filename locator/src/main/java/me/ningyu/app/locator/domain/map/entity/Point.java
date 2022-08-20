package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;
import me.ningyu.app.locator.domain.device.entity.Device;

import javax.persistence.Entity;
import javax.persistence.*;

@Entity
@Table(name = "locator_point")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Point extends Variable
{
    @Column(name = "latitude", columnDefinition = "int(11) DEFAULT 0 COMMENT '纬度'")
    private Long latitude;

    @Column(name = "longitude", columnDefinition = "int(11) DEFAULT 0 COMMENT '经度'")
    private Long longitude;

    @Column(name = "loc_time", columnDefinition = "int(11) DEFAULT 0 COMMENT '定位时设备的时间(Unix时间戳)'")
    private Long locTime;

    /**
     * 坐标类型
     */
    private CoordType coordTypeInput = CoordType.bd09ll;

    /**
     * 速度：单位km/h
     */
    private Double speed;


    @Column(name = "direction", columnDefinition = "int(11) DEFAULT 0 COMMENT '方向：范围为[0,359]，0度为正北方向，顺时针'")
    private int direction;

    @Column(name = "height", columnDefinition = "int(11) DEFAULT 0 COMMENT '高度：单位米'")
    private Double height;

    @Column(name = "radius", columnDefinition = "int(11) DEFAULT 0 COMMENT '定位精度，GPS或定位SDK返回的值，单位米'")
    private Double radius;

    @Column(name = "address", columnDefinition = "VARCHAR(100) DEFAULT 0 COMMENT '位置'")
    private String address;

    /**
     * 上报坐标的设备
     */
    @ManyToOne
    private Device device;
}
