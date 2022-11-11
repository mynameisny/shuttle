package me.ningyu.app.locator.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.locator.common.enums.Coordinate;
import me.ningyu.app.locator.domain.map.entity.Address;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto
{
    @ApiModelProperty("地址编码")
    @NotBlank
    private String code;

    @ApiModelProperty("地址名称")
    @NotBlank
    private String name;

    @ApiModelProperty("坐标系")
    private Coordinate coordinate;

    @ApiModelProperty("纬度")
    @NotNull
    private Float latitude;

    @ApiModelProperty("经度")
    @NotNull
    private Float longitude;

    @ApiModelProperty("地区编码")
    private String areaCode;

    @ApiModelProperty("地区图片")
    private List<String> imageURLs;

    @ApiModelProperty("地址描述")
    private String description;

    public static AddressDto buildFromEntity(Address entity)
    {
        return AddressDto.builder()
                         .code(entity.getCode())
                         .name(entity.getName())
                         .coordinate(entity.getCoordinate())
                         .latitude(entity.getLatitude())
                         .longitude(entity.getLongitude())
                         .areaCode(entity.getAreaCode())
                         .imageURLs(entity.getImageURL())
                         .description(entity.getDescription())
                         .build();
    }
}
