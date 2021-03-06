package me.ningyu.app.locator.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("坐标对象")
public class PointDto
{
    @ApiModelProperty(value = "坐标的ID")
    private String id;

    @ApiModelProperty(value = "纬度")
    private Long latitude;

    @ApiModelProperty("经度")
    private Long longitude;

    @ApiModelProperty("定位时设备的时间(Unix时间戳)")
    private Long locTime;

    @ApiModelProperty("设备ID")
    private String deviceId;
}
