package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * 站点图片
 * </pre>
 */
@Entity(name = "stop_photo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopPhoto extends AbstractAuditable
{
    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '文件名称，如“stop_001_entrance.jpg”'")
    private String filename;

    @Column(name = "storage_path", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '存储路径，如“/uploads/stops/ST001/xxx.jpg”'")
    private String storagePath;

    @Column(name = "url", columnDefinition = "VARCHAR(100) COMMENT '可选：CDN 或静态资源 URL'")
    private String publicUrl;

    @Column(columnDefinition = "TEXT COMMENT '描述，如“东门入口正视图”'")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false, columnDefinition = "BIGINT COMMENT '站点ID'")
    private PhysicalStop stop;
}
