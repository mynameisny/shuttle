package me.ningyu.app.locator.domain.vehicle.entity;

import lombok.Data;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;

/**
 * 车型
 */
@Table(name = "locator_model")
@Entity
@Data
public class Model extends Variable
{
    /**
     * 中文名称
     */
    private String chineseName;

    /**
     * 英文名称
     */
    private String englishName;

    /**
     * 简介
     */
    private String brief;

    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;
}
