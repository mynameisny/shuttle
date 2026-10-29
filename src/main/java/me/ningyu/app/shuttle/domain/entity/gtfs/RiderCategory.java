package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 乘客类别（对应 GTFS rider_categories.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#rider_categoriestxt">rider_categories.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 定义乘客类别（如老年人、学生）（Fares V2）。
 * - rider_category_id        (Unique ID) [必填] 乘客类别标识。
 * - rider_category_name      (Text)      [必填] 乘客类别名称。
 * - is_default_fare_category (Enum)      [必填] 是否默认票价类别（0=否,1=是）；同一票价产品的多个类别须恰好一个默认。
 * - eligibility_url          (URL)       [可选] 资格说明页面。
 * </pre>
 */
@Entity(name = "rider_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderCategory extends AbstractAuditable
{
    @Column(name = "rider_category_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '乘客类别标识'")
    private String riderCategoryId;
    @Column(name = "rider_category_name", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '乘客类别名称'")
    private String riderCategoryName;
    @Column(name = "is_default_fare_category", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '是否默认票价类别' DEFAULT 0")
    @Builder.Default
    private Boolean isDefaultFareCategory = false;
    @Column(name = "eligibility_url", columnDefinition = "VARCHAR(255) COMMENT '资格说明页面地址'")
    private String eligibilityUrl;
}
