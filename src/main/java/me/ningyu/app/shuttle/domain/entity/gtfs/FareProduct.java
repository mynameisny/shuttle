package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * <pre>
 * 票价产品（对应 GTFS fare_products.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#fare_productstxt">fare_products.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 定义可购买的票价产品（Fares V2），与 Fares V1（fare_attributes/fare_rules）相互独立。
 * - fare_product_id           (ID)             [必填]     票价产品标识，同一产品可关联多个乘客类别（多行）。
 * - fare_product_name        (Text)           [可选]     票价产品名称，面向乘客展示。
 * - fare_product_description (Text)           [可选]     票价产品描述。
 * - amount                   (Currency)       [条件必填] 票价金额（默认票价类别机制下可省略）。
 * - currency                 (Currency code)  [条件必填] 货币代码（如 CNY）。
 * - rider_category_id        (Foreign ID)     [条件必填] 关联的乘客类别（引用 rider_categories）。
 * - fare_media_id            (Foreign ID)     [可选]     使用的票价媒介（引用 fare_media）。
 * 注：引用字段为弱引用（GTFS CSV 语义），不用数据库外键。
 * </pre>
 */
@Entity(name = "fare_product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareProduct extends AbstractAuditable
{
    @Column(name = "fare_product_id", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '票价产品标识，同一产品可多行对应不同乘客类别'")
    private String fareProductId;
    @Column(name = "fare_product_name", columnDefinition = "VARCHAR(100) COMMENT '票价产品名称'")
    private String fareProductName;
    @Column(name = "fare_product_description", columnDefinition = "VARCHAR(255) COMMENT '票价产品描述'")
    private String fareProductDescription;
    @Column(name = "amount", columnDefinition = "DECIMAL(10,2) COMMENT '票价金额'")
    private BigDecimal amount;
    @Column(name = "currency", columnDefinition = "VARCHAR(10) COMMENT '货币代码，如 CNY'")
    private String currency;
    @Column(name = "rider_category_id", columnDefinition = "VARCHAR(50) COMMENT '乘客类别标识，引用 rider_categories'")
    private String riderCategoryId;
    @Column(name = "fare_media_id", columnDefinition = "VARCHAR(50) COMMENT '票价媒介标识，引用 fare_media'")
    private String fareMediaId;
}
