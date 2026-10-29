package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 路线-网络分配（对应 GTFS route_networks.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#route_networkstxt">route_networks.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 将路线分配到网络的规则；与 routes.network_id 互斥。
 * - route_id  (Foreign ID) [必填] 分配的路线。
 * - network_id(Foreign ID) [必填] 所属网络。
 * </pre>
 */
@Entity(name = "route_network")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteNetwork extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '分配的路线'")
    private Route route;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "network_id", referencedColumnName = "network_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '所属网络'")
    private Network network;
}
