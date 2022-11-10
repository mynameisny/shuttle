package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.common.enums.StationStatus;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locator_station")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Station extends Variable
{
    @Column(name = "code", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE COMMENT '站点编码'")
    private String code;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '站点名称'")
    private String name;

    /**
     * 站点地址：多个站点可以对应同一个地址
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "locator_station_address",
               joinColumns = @JoinColumn(name = "station_id"),
               inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private Address address;

    @Column(name = "description", columnDefinition = "TEXT COMMENT '站点描述'")
    private String description;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '站点状态'")
    @Enumerated(value = EnumType.STRING)
    private StationStatus status;
}
