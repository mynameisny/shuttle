package me.ningyu.app.shuttle.domain.entity.gtfs;

import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * <pre>
 * 服务日历（对应 GTFS calendar.txt）
 * 定义每周哪些天提供服务，配合起止日期形成服务日期范围
 *
 * 参考：<a href="https://gtfs.org/documentation/schedule/reference/#calendartxt">calendar.txt</a>
 *
 * GTFS 字段定义（Field Name / Type / Presence）：
 * - service_id (Unique ID) [必填] 服务标识。
 * - monday     (Enum)      [必填] 周一运营（1=是,0=否）。
 * - tuesday    (Enum)      [必填] 周二运营。
 * - wednesday  (Enum)      [必填] 周三运营。
 * - thursday   (Enum)      [必填] 周四运营。
 * - friday     (Enum)      [必填] 周五运营。
 * - saturday   (Enum)      [必填] 周六运营。
 * - sunday     (Enum)      [必填] 周日运营。
 * - start_date (Date)      [必填] 服务开始日期（YYYYMMDD）。
 * - end_date   (Date)      [必填] 服务结束日期（含当天）。
 *
 * Java 字段映射：
 * - service_id → serviceId
 * - monday~sunday → monday~sunday（Boolean，true 对应 GTFS 的 1）
 * - start_date → startDate
 * - end_date → endDate
 *
 * 项目扩展字段（非 GTFS 规范）：
 * - agency / code / name：所属机构、服务编码、服务名称，用于业务展示与查询。
 * </pre>
 */
@Entity(name = "service_calendar")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCalendar extends AbstractAuditable
{
    /**
     * GTFS service_id：服务唯一标识，被 trips.service_id 引用。
     */
    @Column(name = "service_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '服务标识，确定服务可用日期集合'")
    private String serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属机构（业务扩展）'")
    private Agency agency;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '服务编码，如weekday、weekend、holiday'")
    private String code;

    @Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '服务名称，如工作日服务、周末服务'")
    private String name;

    @Column(name = "monday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周一是否运营'")
    @Builder.Default
    private Boolean monday = false;

    @Column(name = "tuesday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周二是否运营'")
    @Builder.Default
    private Boolean tuesday = false;

    @Column(name = "wednesday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周三是否运营'")
    @Builder.Default
    private Boolean wednesday = false;

    @Column(name = "thursday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周四是否运营'")
    @Builder.Default
    private Boolean thursday = false;

    @Column(name = "friday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周五是否运营'")
    @Builder.Default
    private Boolean friday = false;

    @Column(name = "saturday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周六是否运营'")
    @Builder.Default
    private Boolean saturday = false;

    @Column(name = "sunday", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '周日是否运营'")
    @Builder.Default
    private Boolean sunday = false;

    @Column(name = "start_date", nullable = false, columnDefinition = "DATE COMMENT '服务开始日期'")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, columnDefinition = "DATE COMMENT '服务结束日期（含当天）'")
    private LocalDate endDate;
}
