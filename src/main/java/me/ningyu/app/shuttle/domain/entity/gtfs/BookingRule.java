package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import me.ningyu.app.shuttle.enums.BookingType;

/**
 * <pre>
 * 预订规则（对应 GTFS booking_rules.txt）
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#booking_rulestxt">booking_rules.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * 面向乘客请求式服务的预订信息，被 stop_times 的 pickup/drop_off_booking_rule_id 引用。
 * - booking_rule_id            (Unique ID) [必填]     预订规则标识。
 * - booking_type               (Enum)      [必填]     预订方式（0=实时,1=当日,2=提前预约）。
 * - prior_notice_duration_min  (Integer)   [条件必填] 提前预约最少时长（分钟）。
 * - prior_notice_duration_max  (Integer)   [条件必填] 提前预约最多时长（分钟）。
 * - prior_notice_last_day      (Integer)   [条件必填] 可预订的最后一日（旅行前天数）。
 * - prior_notice_last_time     (Time)      [条件必填] 最后一日可预订的时刻。
 * - prior_notice_start_day     (Integer)   [条件必填] 可开始预订的最早日（旅行前天数）。
 * - prior_notice_start_time    (Time)      [条件必填] 最早日可开始预订的时刻。
 * - drop_off_window            (Time)      [可选]     下车服务时间窗。
 * - pickup_window              (Time)      [可选]     上车服务时间窗。
 * - message                    (Text)      [可选]     预订流程说明。
 * - pickup_message             (Text)      [可选]     上车预订流程说明。
 * - drop_off_message           (Text)      [可选]     下车预订流程说明。
 * - phone_number               (Phone)     [可选]     预订电话。
 * - pickup_phone_number        (Phone)     [可选]     上车预订电话。
 * - drop_off_phone_number      (Phone)     [可选]     下车预订电话。
 * - info_url                   (URL)       [可选]     预订说明页面。
 * - pickup_info_url            (URL)       [可选]     上车预订说明页面。
 * - drop_off_info_url          (URL)       [可选]     下车预订说明页面。
 * - booking_url                (URL)       [可选]     在线预订页面。
 * </pre>
 */
@Entity(name = "booking_rule")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRule extends AbstractAuditable
{
    @Column(name = "booking_rule_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '预订规则标识'")
    private String bookingRuleId;
    /**
     * 预订方式。
     * REALTIME - 实时预订
     * SAME_DAY - 当日预订
     * ADVANCE - 提前预约
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'REALTIME' COMMENT '预订方式：REALTIME=实时，SAME_DAY=当日，ADVANCE=提前预约'")
    @Builder.Default
    private BookingType bookingType = BookingType.REALTIME;
    @Column(name = "prior_notice_duration_min", columnDefinition = "INT COMMENT '提前预约最少时长（分钟）'")
    private Integer priorNoticeDurationMin;
    @Column(name = "prior_notice_duration_max", columnDefinition = "INT COMMENT '提前预约最多时长（分钟）'")
    private Integer priorNoticeDurationMax;
    @Column(name = "prior_notice_last_day", columnDefinition = "INT COMMENT '可预订的最后一日（旅行前天数）'")
    private Integer priorNoticeLastDay;
    @Column(name = "prior_notice_last_time", columnDefinition = "TIME COMMENT '最后一日可预订的时刻'")
    private LocalTime priorNoticeLastTime;
    @Column(name = "prior_notice_start_day", columnDefinition = "INT COMMENT '可开始预订的最早日（旅行前天数）'")
    private Integer priorNoticeStartDay;
    @Column(name = "prior_notice_start_time", columnDefinition = "TIME COMMENT '最早日可开始预订的时刻'")
    private LocalTime priorNoticeStartTime;
    @Column(name = "drop_off_window", columnDefinition = "TIME COMMENT '下车服务时间窗'")
    private LocalTime dropOffWindow;
    @Column(name = "pickup_window", columnDefinition = "TIME COMMENT '上车服务时间窗'")
    private LocalTime pickupWindow;
    @Column(name = "message", columnDefinition = "VARCHAR(500) COMMENT '预订流程说明'")
    private String message;
    @Column(name = "pickup_message", columnDefinition = "VARCHAR(500) COMMENT '上车预订流程说明'")
    private String pickupMessage;
    @Column(name = "drop_off_message", columnDefinition = "VARCHAR(500) COMMENT '下车预订流程说明'")
    private String dropOffMessage;
    @Column(name = "phone_number", columnDefinition = "VARCHAR(50) COMMENT '预订电话'")
    private String phoneNumber;
    @Column(name = "pickup_phone_number", columnDefinition = "VARCHAR(50) COMMENT '上车预订电话'")
    private String pickupPhoneNumber;
    @Column(name = "drop_off_phone_number", columnDefinition = "VARCHAR(50) COMMENT '下车预订电话'")
    private String dropOffPhoneNumber;
    @Column(name = "info_url", columnDefinition = "VARCHAR(255) COMMENT '预订说明页面'")
    private String infoUrl;
    @Column(name = "pickup_info_url", columnDefinition = "VARCHAR(255) COMMENT '上车预订说明页面'")
    private String pickupInfoUrl;
    @Column(name = "drop_off_info_url", columnDefinition = "VARCHAR(255) COMMENT '下车预订说明页面'")
    private String dropOffInfoUrl;
    @Column(name = "booking_url", columnDefinition = "VARCHAR(255) COMMENT '在线预订页面'")
    private String bookingUrl;
}
