package me.ningyu.app.locator.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class StationDto
{
    @ApiModelProperty("站点编码")
    @NotBlank
    private String code;

    @ApiModelProperty("站点名称")
    @NotBlank
    private String name;

    @ApiModelProperty("站点地址")
    private AddressDto address;

    @ApiModelProperty("站点描述")
    private String description;
}
