package me.ningyu.app.shuttle.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置。
 *
 * <p>存储目录可配置化（可用环境变量 UPLOAD_DIR 覆盖），数据库/前端只存相对 URL，
 * 物理目录与 URL 解耦，迁移环境时只需拷目录 + 改配置。
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties
{
    /**
     * 存储根目录（相对或绝对路径）。
     */
    private String dir = "./data/uploads";

    /**
     * 压缩后图片最大宽（px）。
     */
    private int maxWidth = 1024;

    /**
     * 压缩后图片最大高（px）。
     */
    private int maxHeight = 1024;

    /**
     * JPEG 压缩质量（0~1）。
     */
    private float quality = 0.8f;
}
