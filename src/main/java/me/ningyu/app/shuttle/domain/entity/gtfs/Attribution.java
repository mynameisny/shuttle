package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 数据集署名（对应 GTFS attributions.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#attributionstxt">attributions.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 数据集或其子集（机构/线路/行程）的署名信息。
 * - attribution_id    (Unique ID) [条件必填] 署名标识（定义范围字段时必填）。
 * - attribution_text  (Text)      [必填]     署名文本。
 * - attribution_url   (URL)       [可选]     署名指向的地址。
 * - attribution_email (Email)     [可选]     署名联系邮箱。
 * - attribution_phone (Phone)     [可选]     署名联系电话。
 * - agency_id / route_id / trip_id / organization_id [条件禁用] 署名适用范围，四者互斥，
 *   全部为空时适用于整个数据集。
 * </pre>
 */
@Entity(name = "attribution")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribution extends AbstractAuditable
{
    @Column(name = "attribution_id", columnDefinition = "VARCHAR(50) COMMENT '署名标识'")
    private String attributionId;
    @Column(name = "attribution_text", nullable = false, columnDefinition = "VARCHAR(500) COMMENT '署名文本'")
    private String attributionText;
    @Column(name = "attribution_url", columnDefinition = "VARCHAR(255) COMMENT '署名指向的地址'")
    private String attributionUrl;
    @Column(name = "attribution_email", columnDefinition = "VARCHAR(100) COMMENT '署名联系邮箱'")
    private String attributionEmail;
    @Column(name = "attribution_phone", columnDefinition = "VARCHAR(50) COMMENT '署名联系电话'")
    private String attributionPhone;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", referencedColumnName = "agency_id", columnDefinition = "VARCHAR(50) COMMENT '署名适用的机构（与范围字段互斥）'")
    private Agency agency;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", columnDefinition = "VARCHAR(50) COMMENT '署名适用的线路（与范围字段互斥）'")
    private Route route;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", referencedColumnName = "trip_id", columnDefinition = "VARCHAR(50) COMMENT '署名适用的行程（与范围字段互斥）'")
    private Trip trip;
    @Column(name = "organization_id", columnDefinition = "VARCHAR(50) COMMENT '署名适用的组织（GTFS 数据集外的组织，与范围字段互斥）'")
    private String organizationId;
}
