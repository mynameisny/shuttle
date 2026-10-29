package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 路线网络（对应 GTFS networks.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#networkstxt">networks.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 路线的网络分组；与 routes.network_id 互斥（存在本文件时 routes.network_id 禁止使用）。
 * - network_id   (Unique ID) [必填]     网络标识。
 * - network_name (Text)      [条件必填] 网络名称，面向乘客展示。
 * </pre>
 */
@Entity(name = "network")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Network extends AbstractAuditable
{
    @Column(name = "network_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '网络标识'")
    private String networkId;
    @Column(name = "network_name", columnDefinition = "VARCHAR(100) COMMENT '网络名称'")
    private String networkName;
}
