package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.VehicleStatus;

/**
 * <pre>
 * 车辆
 * 支持：公交车、班车、商务车、大巴等
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属组织ID'")
    private Organization organization;

    @Column(name = "plate_no", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '车牌号，如京N3RJ66'")
    private String plateNo;

    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '车辆类型（公交车/班车/大巴/商务车）'")
    private String type;

    @Column(name = "seat_count", columnDefinition = "INT COMMENT '座位数'")
    private Integer seatCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '车辆状态'")
    @Builder.Default
    private VehicleStatus status = VehicleStatus.ACTIVE;
}
