package me.ningyu.app.locator.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "locator_passenger")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends User
{
    @Column(unique = true)
    private String staffNo;

    /**
     * 是否为车长
     */
    private boolean isConductor;
}
