package me.ningyu.app.locator.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto
{
    /**
     * 线路名称
     */
    private String name;

    /**
     * 线路编号
     */
    private String code;

    /**
     * 线路描述
     */
    private String description;

    /**
     * 表示线路的颜色
     */
    private String colorHex;
}
