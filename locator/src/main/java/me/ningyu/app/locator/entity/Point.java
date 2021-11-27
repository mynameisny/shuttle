package me.ningyu.app.locator.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.*;

@Table(name = "point")
@Entity
@Data
public class Point
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 纬度
     */
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
     *  entity移动所产生的连续轨迹被称为track，track由一系列轨迹点（point）组成
     */
    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    /**
     * 位置
     */
    private String address;
}
