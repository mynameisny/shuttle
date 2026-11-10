package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.storage.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传（图片）。
 *
 * <p>业务侧通过 {@code dir} 指定用途子目录（如 avatars、stops），
 * 返回相对 URL，由调用方存库。
 */
@Tag(name = "文件上传")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UploadController
{
    private final StorageService storageService;

    @PostMapping("/upload")
    @Operation(summary = "上传图片", description = "压缩后存盘，返回可访问的相对 URL")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "common") String dir)
    {
        String url = storageService.store(file, dir);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
