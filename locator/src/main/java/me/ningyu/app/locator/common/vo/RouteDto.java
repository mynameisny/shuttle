package me.ningyu.app.locator.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.locator.common.enums.RouteStatus;

import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto
{
    /**
     * 线路编号
     */
    @NotBlank(message = "线路编号不能为空")
    private String code;

    /**
     * 线路名称
     */
    @NotBlank(message = "线路名称不能为空")
    private String name;

    /**
     * 线路描述
     */
    private String description;

    /**
     * 表示线路的颜色
     */
    private String colorHex;

    /**
     * 线路起点
     */
    private String origin;

    /**
     * 线路终点
     */
    private String terminal;

    /**
     * 线路状态
     */
    private RouteStatus status;

    /**
     * 组成线路的站点
     */
    @OneToMany
    private List<StationDto> stations;
}
