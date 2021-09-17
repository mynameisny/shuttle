package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "passenger")
@Entity
@Data
public class Passenger extends BaseUser
{
    /**
     * 员工编号
     */
    @Column(unique = true)
    private String staffNo;
    
    /**
     * 是否为车长
     */
    private boolean isConductor;
}
