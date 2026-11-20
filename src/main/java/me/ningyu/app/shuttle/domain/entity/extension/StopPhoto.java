package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;

/**
 * 站点照片（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>每个站点可挂多张照片，一行一个 URL；通过 stop_id（GTFS stops.txt 主键字符串）外键关联，
 * <b>不修改 gtfs 包的 {@code Stop} 实体</b>（项目红线：GTFS 实体只能扩展不能改）。
 */
@Entity(name = "stop_photo")
@Table(name = "stop_photo", indexes = @Index(name = "idx_stop_photo_stop_id", columnList = "stop_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopPhoto extends AbstractAuditable
{
    /**
     * 关联站点（GTFS stop_id 字符串，对应 stops.stop_id）。
     */
    @Column(name = "stop_id", nullable = false, length = 50, columnDefinition = "VARCHAR(50) COMMENT '关联站点 GTFS stop_id'")
    private String stopId;

    /**
     * 照片 URL（相对路径，如 /uploads/stops/2026/11/xxx.jpg）。
     */
    @Column(name = "url", nullable = false, length = 255, columnDefinition = "VARCHAR(255) COMMENT '照片 URL（相对路径）'")
    private String url;

    /**
     * 排序号（同一站点内小号在前）。
     */
    @Column(name = "sort_order", nullable = false, columnDefinition = "INT DEFAULT 0 COMMENT '站内排序号'")
    @Builder.Default
    private Integer sortOrder = 0;
}
