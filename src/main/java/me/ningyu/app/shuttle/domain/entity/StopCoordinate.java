package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.CoordinateSystem;

import java.math.BigDecimal;

/**
 * <pre>
 * 站点坐标
 * </pre>
 */
@Entity(name = "stop_coordinate")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopCoordinate extends AbstractAuditable
{
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '坐标系类型'")
    private CoordinateSystem type;

    @Column(nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '纬度'")
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '经度'")
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '站点ID'")
    private Stop stop;
}
