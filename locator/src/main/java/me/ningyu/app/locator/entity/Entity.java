package me.ningyu.app.locator.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Entity代表现实中的一个终端用户，可以是一个人、车或任何运动的物体。
 * <br>
 * 终端管理类接口主要实现：entity的创建、更新、删除、查询。例如：添加一辆车、删除一辆车、更新车辆的属性信息（如：车辆所属运营区）等。
 */
@Table
@javax.persistence.Entity
@Data
public class Entity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * entity名称，作为其唯一标识
     */
    private String name;

    /**
     * entity 的可读性描述
     */
    private String description;
}
