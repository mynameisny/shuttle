package me.ningyu.app.easymonger.domain.coupon;

import lombok.*;
import me.ningyu.app.easymonger.model.enums.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"x", "y", "width", "height"})
public class Coordinate
{
    /**
     * 字段标识
     */
    private Field field;

    /**
     * 图片左上角，距0点的横向距离
     */
    private int x;

    /**
     * 图片左上角，距0点的纵向距离
     */
    private int y;

    /**
     * 图片的宽度
     */
    private int width;

    /**
     * 图片的高度
     */
    private int height;
}
