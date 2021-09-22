package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;

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
