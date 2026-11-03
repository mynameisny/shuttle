package me.ningyu.app.shuttle.model.stop;

import lombok.*;

import java.util.List;

/**
 * 站点引用摘要视图（管理端站点维护页列表消歧）。
 * 批量返回各站点被哪些线路/方向引用（同线路同方向去重），
 * 用于站点台账列表项标注 [线路·方向] 角标、识别孤儿站点；
 * 无引用的站点不出现在结果中（前端据缺失标记"未使用"）。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StopUsageSummaryView
{
    private Long stopPk;
    private List<Ref> refs;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ref
    {
        private Long routePk;
        private String routeShortName;
        private Integer directionId;
    }
}
