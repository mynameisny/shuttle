package me.ningyu.app.locator.vo;

import lombok.Data;
import me.ningyu.app.locator.entity.CoordType;
import me.ningyu.app.locator.entity.Track;

import javax.persistence.*;

@Data
public class PointDto
{
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
}
