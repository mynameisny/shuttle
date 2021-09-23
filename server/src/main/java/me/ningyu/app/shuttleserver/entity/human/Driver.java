package me.ningyu.app.shuttleserver.entity.human;

import lombok.Data;

import javax.persistence.*;

/**
 * 司机
 */
@Table(name = "driver", uniqueConstraints = {@UniqueConstraint(name = "UniqueNameAndMobile", columnNames = {"name", "mobile"})})
@Entity
@Data
public class Driver extends BaseUser
{
    /**
     * 驾龄
     */
    private int drivingExperience;

}
