package me.ningyu.app.locator.domain.map.entity;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locator_address")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Address extends Variable
{
    @Column(name = "name", columnDefinition = "VARCHAR(100) DEFAULT '' COMMENT '地点名称'")
    private String name;

    @Column(name = "coordinate", columnDefinition = "VARCHAR(50) DEFAULT '' COMMENT '坐标系'")
    private String coordinate;

    @Column(name = "latitude", columnDefinition = "FLOAT(11) DEFAULT 0.0 COMMENT '纬度'")
    private Float latitude;

    @Column(name = "longitude", columnDefinition = "FLOAT(11) DEFAULT 0.0 COMMENT '经度'")
    private Float longitude;

    @Column(name = "zip_code", columnDefinition = "VARCHAR(10) DEFAULT '' COMMENT '邮政编码'")
    private String zipCode;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "locator_address_image", joinColumns = @JoinColumn(name = "address_id"))
    @Column(name = "image_url", nullable = false)
    private List<String> imageURL;
}
