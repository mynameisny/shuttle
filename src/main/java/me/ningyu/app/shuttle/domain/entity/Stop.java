package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 物理站点
 * “物理站点”是地理实体，不应包含任何与“线路”或“时间”相关的业务属性
 * </pre>
 */
@Entity(name = "stop")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stop extends AbstractAuditable
{
    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '站点编码'")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '站点名称，如“朱辛庄新区南区-西门”'")
    private String name;

    @Column(columnDefinition = "TEXT COMMENT '描述，如“朱辛庄东路与北清路交汇处向北15米”'")
    private String description;

    // 关联多个坐标（不同坐标系）
    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StopCoordinate> coordinates = new ArrayList<>();

    // 关联多张实拍图片
    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StopPhoto> photos = new ArrayList<>();

    @Column(name = "sequence", columnDefinition = "INT(11) COMMENT '自定义排序编号'")
    private Integer sequence = 0;


    // 便捷方法：添加坐标
    public void addCoordinate(StopCoordinate coordinate)
    {
        coordinate.setStop(this);
        this.coordinates.add(coordinate);
    }

    // 便捷方法：添加照片
    public void addPhoto(StopPhoto photo)
    {
        photo.setStop(this);
        this.photos.add(photo);
    }
}
