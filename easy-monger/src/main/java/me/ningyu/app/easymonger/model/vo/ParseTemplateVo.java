package me.ningyu.app.easymonger.model.vo;

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
public class ParseTemplateVo implements Serializable
{
    /**
     * 账号属于哪个账户
     */
    private String accountId;

    /**
     * 解析模板ID
     */
    private String id;

    /**
     * 解析模板名称
     */
    private String name;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 坐标
     */
    // private List<Coordinate> coordinates;
    //
    /**
     * 备注（预留字段）
     */
    private String remark;
}