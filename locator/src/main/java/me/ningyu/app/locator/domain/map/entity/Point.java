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

    /**
     * 经度
     */
    private Long longitude;

    /**
     * 定位时设备的时间
     * Unix时间戳
     */
    private Long locTime;

    /**
     * 坐标类型
     */
    private CoordType coordTypeInput = CoordType.bd09ll;

    /**
     * 速度：单位km/h
     */
    private Double speed;

    /**
     * 方向：范围为[0,359]，0度为正北方向，顺时针
     */
    private int direction;

    /**
     * 高度：单位米
     */
    private Double height;

    /**
     * 定位精度，GPS或定位SDK返回的值，单位米
     */
    private Double radius;

    /**
     * 位置
     */
    private String address;

    /**
     * 上报坐标的设备
     */
    @ManyToOne
    private Device device;
}
