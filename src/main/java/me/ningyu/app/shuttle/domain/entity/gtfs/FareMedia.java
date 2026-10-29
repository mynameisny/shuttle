package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.FareMediaType;

/**
 * <pre>
 * 票价媒介（对应 GTFS fare_media.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#fare_mediatxt">fare_media.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 标识使用票价产品的物理或虚拟载体。
 * - fare_media_id   (Unique ID) [必填] 票价媒介标识。
 * - fare_media_name (Text)      [可选] 票价媒介名称（交通卡/移动应用应与机构面向乘客的名称一致）。
 * - fare_media_type (Enum)      [必填] 票价媒介类型（0=无媒介,1=纸质票,2=交通卡,3=非接触EMV,4=移动应用）。
 * </pre>
 */
@Entity(name = "fare_media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareMedia extends AbstractAuditable
{
    @Column(name = "fare_media_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '票价媒介标识'")
    private String fareMediaId;
    @Column(name = "fare_media_name", columnDefinition = "VARCHAR(100) COMMENT '票价媒介名称'")
    private String fareMediaName;
        /**
     * 票价媒介类型。
* NONE - 无媒介（现金等）
* PAPER_TICKET - 实体纸质车票
* TRANSIT_CARD - 实体交通卡
* CEMV - 非接触式EMV卡
* MOBILE_APP - 移动应用
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fare_media_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'NONE' COMMENT '票价媒介类型：NONE/PAPER_TICKET/TRANSIT_CARD/CEMV/MOBILE_APP'")
    @Builder.Default
    private FareMediaType fareMediaType = FareMediaType.NONE;
}
