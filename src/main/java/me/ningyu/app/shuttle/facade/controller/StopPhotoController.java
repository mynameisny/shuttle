package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.facade.service.StopPhotoService;
import me.ningyu.app.shuttle.facade.service.StopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 站点照片接口（GTFS 扩展域）。
 * 路径参数为站点数据库主键，内部换算为 GTFS stop_id 存取扩展表，不动 gtfs 包。
 */
@Tag(name = "站点照片")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/stops/{id}/photos")
@RequiredArgsConstructor
public class StopPhotoController
{
    private final StopPhotoService stopPhotoService;
    private final StopService stopService;

    @GetMapping
    @Operation(summary = "站点照片列表", description = "返回该站点全部照片 URL（按排序号）")
    public ResponseEntity<List<String>> list(
            @Parameter(description = "站点数据库主键") @PathVariable Long id)
    {
        String stopId = stopService.getStopById(id).getStopId();
        return ResponseEntity.ok(stopPhotoService.getUrls(stopId));
    }

    @PutMapping
    @Operation(summary = "全量覆盖站点照片", description = "以传入顺序覆盖该站点的照片列表；空列表表示清空")
    public ResponseEntity<List<String>> replace(
            @Parameter(description = "站点数据库主键") @PathVariable Long id,
            @RequestBody Map<String, List<String>> body)
    {
        String stopId = stopService.getStopById(id).getStopId();
        return ResponseEntity.ok(stopPhotoService.replace(stopId, body.get("urls")));
    }
}
