package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.Direction;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 带方向的线路
 * </pre>
 */
@Entity(name = "route_direction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDirection extends AbstractAuditable
{
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '线路编码'")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '方向（上行/下行/环线）'")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(columnDefinition = "TEXT COMMENT '描述，如“回龙观 - 昌平科技园”'")
    private String description;

    @OneToMany(mappedBy = "directionalRoute", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<RouteDirectionStop> stops = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false, columnDefinition = "BIGINT COMMENT '线路ID'")
    private Route route;
}
