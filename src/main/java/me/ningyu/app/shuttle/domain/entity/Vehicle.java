package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

/**
 * <pre>
 * 车俩
 * </pre>
 */
@Entity(name = "vehicle")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends AbstractAuditable
{
    @Column(name = "brand", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '品牌，例如“金龙”'")
    private String brand;

    @Column(name = "series", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '车系，例如“凯锐”'")
    private String series;

    @Column(name = "model", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '车型，例如“浩克”'")
    private String model;

    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '类型（大巴/中巴/小巴）'")
    private String type;

    @Column(name = "license_plate", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '车牌号，例如“京N3RJ66”'")
    private String licensePlate;

    @Column(name = "seat_count", columnDefinition = "INT(11) COMMENT '座位数'")
    private Integer seatCount;

    @Column(name = "supplier", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '供应商，例如“新朗途客运有限公司”'")
    private String supplier;
}
