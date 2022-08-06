package me.ningyu.app.locator.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "locator_driver")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends User
{
    @Column(name = "organization", columnDefinition = "int(11) DEFAULT 0 COMMENT '驾龄'")
    private int drivingExperience;
}
