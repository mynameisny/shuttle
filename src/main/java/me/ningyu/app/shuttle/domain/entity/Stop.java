package me.ningyu.app.shuttle.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 停靠点（对应 GTFS stops.txt）
 * 表示车辆真正停车的位置，包含 GPS、所属站点、所在道路、所在方向
 *
 * 一个 StopArea 可包含多个 Stop
 * 例如：StopArea"地铁朱辛庄站" → Stop"东侧站台"(GPS A) + Stop"西侧站台"(GPS B)
 *
 * RouteStop 永远关联 Stop，而不是 StopArea
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_area_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属站点区域ID'")
    private StopArea stopArea;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '停靠点名称，如东侧站台'")
    private String name;

    @Column(nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '经度'")
    private BigDecimal longitude;

    @Column(nullable = false, precision = 10, scale = 7, columnDefinition = "DECIMAL(10,7) COMMENT '纬度'")
    private BigDecimal latitude;

    @Column(columnDefinition = "VARCHAR(50) COMMENT '站台编号'")
    private String platform;

    @Column(columnDefinition = "DECIMAL(5,2) COMMENT '方位角(0-360度)'")
    private BigDecimal bearing;

    @Column(columnDefinition = "TEXT COMMENT '备注'")
    private String remark;

    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StopPhoto> photos = new ArrayList<>();

    public void addPhoto(StopPhoto photo)
    {
        photo.setStop(this);
        this.photos.add(photo);
    }
}
