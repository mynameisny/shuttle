package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.StopPhoto;
import me.ningyu.app.shuttle.domain.repo.StopPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 站点照片服务：管理 GTFS 扩展表 stop_photo（一站点多张照片）。
 * gtfs 包的 Stop 实体不做任何改动（项目红线）。
 */
@Service
@RequiredArgsConstructor
public class StopPhotoService
{
    private final StopPhotoRepository stopPhotoRepository;

    @Transactional(readOnly = true)
    public List<String> getUrls(String stopId)
    {
        return stopPhotoRepository.findByStopIdOrderBySortOrderAscIdAsc(stopId)
                .stream().map(StopPhoto::getUrl).toList();
    }

    /**
     * 全量覆盖某站点的照片列表（保持传入顺序作为排序号）。
     *
     * @param stopId GTFS stop_id（站点业务主键，非数据库自增 id）
     * @param urls   照片相对 URL 列表，可为空列表（清空）
     */
    @Transactional
    public List<String> replace(String stopId, List<String> urls)
    {
        List<String> distinct = urls == null ? List.of() : urls.stream().filter(u -> u != null && !u.isBlank()).distinct().toList();
        stopPhotoRepository.deleteByStopId(stopId);
        List<StopPhoto> rows = new ArrayList<>();
        for (int i = 0; i < distinct.size(); i++) {
            rows.add(StopPhoto.builder().stopId(stopId).url(distinct.get(i)).sortOrder(i).build());
        }
        stopPhotoRepository.saveAll(rows);
        return distinct;
    }
}
