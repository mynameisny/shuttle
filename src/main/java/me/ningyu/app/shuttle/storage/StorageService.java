package me.ningyu.app.shuttle.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储抽象。
 *
 * <p>业务代码只依赖本接口，不感知底层是本地磁盘还是对象存储（MinIO/COS/OSS）。
 * 未来上云时新增一个 MinIO 实现并切换即可，业务层零改动。
 */
public interface StorageService
{
    /**
     * 保存文件，返回可访问的相对 URL（如 /uploads/avatars/2026/11/xxx.jpg）。
     *
     * @param file   上传的文件
     * @param subDir 用途子目录（如 avatars、stops）
     */
    String store(MultipartFile file, String subDir);

    /**
     * 按相对 URL 删除文件（URL 不存在则静默忽略）。
     */
    void delete(String url);
}
