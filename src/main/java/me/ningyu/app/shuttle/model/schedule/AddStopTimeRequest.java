package me.ningyu.app.shuttle.model.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 追加停站请求（管理端站点维护页"线路下加站"）。
 * 新站点追加到班次停站序列末尾，stop_sequence 由服务端按现有最大站序 +1 分配；
 * 到/发时间必填（支持 HH:MM 或 HH:MM:SS），到站不得晚于发车。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStopTimeRequest
{
    /** 站点数据库主键（站点须已创建） */
    @NotNull(message = "站点主键不能为空")
    private Long stopPk;

    /** 到站时间（必填，HH:MM 或 HH:MM:SS） */
    @NotBlank(message = "到达时间不能为空")
    private String arrivalTime;

    /** 发车时间（必填，HH:MM 或 HH:MM:SS） */
    @NotBlank(message = "发车时间不能为空")
    private String departureTime;
}
