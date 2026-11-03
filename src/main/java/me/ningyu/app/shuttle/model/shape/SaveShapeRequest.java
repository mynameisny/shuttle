package me.ningyu.app.shuttle.model.shape;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 保存班次形状请求（管理端地图页描线结果）。
 * 语义为全量替换：删除目标 shape_id 的全部旧点后按提交顺序重新插入。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveShapeRequest
{
    /**
     * 形状标识。为空时：优先复用班次既有 shape_id，否则自动生成。
     */
    @Size(max = 50, message = "形状标识长度不能超过50")
    private String shapeId;

    /**
     * 形状顶点，按行驶顺序提交，服务端自动编号 shape_pt_sequence 并累计 shape_dist_traveled。
     */
    @Valid
    @NotEmpty(message = "形状点不能为空")
    @Size(min = 2, message = "形状至少需要2个坐标点")
    private List<ShapePointRequest> points;
}
