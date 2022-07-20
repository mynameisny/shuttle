package me.ningyu.app.locator.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.locator.common.enums.StationStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class StationDto
{
    @ApiModelProperty("站点名称")
    private String name;

    @ApiModelProperty("站点地址")
    private String address;

    @ApiModelProperty("站点描述")
    private String description;

    @ApiModelProperty("纬度")
    private Float latitude;

    @ApiModelProperty("经度")
    private Float longitude;

    @ApiModelProperty("邮编")
    private String zipCode;

    @ApiModelProperty("站点状态")
    private StationStatus status;

    /**
     * 站点图片
     */
    private List<String> imageURL;
}
