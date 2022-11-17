package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.ningyu.app.locator.common.enums.Coordinate;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locator_address")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString
public class Address extends Variable
{
    @Column(name = "code", columnDefinition = "VARCHAR(50) NOT NULL UNIQUE COMMENT '编码'")
    private String code;

    @Column(name = "name", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '名称'")
    private String name;

    @Column(name = "coordinate", columnDefinition = "VARCHAR(50) NOT NULL DEFAULT '' COMMENT '坐标系'")
    @Enumerated(value = EnumType.STRING)
    private Coordinate coordinate;

    @Column(name = "latitude", columnDefinition = "VARCHAR(50) NOT NULL DEFAULT '' COMMENT '纬度'")
    private String latitude;

    @Column(name = "longitude", columnDefinition = "DOUBLE(50) NOT NULL DEFAULT 0.0 COMMENT '经度'")
    private String longitude;

    @Column(name = "area_code", columnDefinition = "DOUBLE(50) NOT NULL DEFAULT 0.0 COMMENT '地区编码，例如220503100001表示鸭园社区'")
    private String areaCode;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "locator_address_image", joinColumns = @JoinColumn(name = "address_id"))
    @Column(name = "image_url", nullable = false)
    private List<String> imageURL;

    @Column(name = "description", columnDefinition = "TEXT COMMENT '描述'")
    private String description;
}
