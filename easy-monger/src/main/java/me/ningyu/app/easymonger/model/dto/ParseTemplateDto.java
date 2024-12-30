package me.ningyu.app.easymonger.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParseTemplateDto implements Serializable
{
    /**
     * 解析模板名称
     */
    private String name;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 坐标
     */
    private List<Coordinate> coordinates;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 备注（预留字段）
     */
    private String remark;
}