package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * <pre>
 * 服务日历（对应 GTFS calendar.txt）
 * 定义哪些日期提供某服务
 *
 * 例如：工作日服务、周末服务、节假日服务
 * 一个 ServiceCalendar 可被多个 Trip 引用
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, columnDefinition = "BIGINT COMMENT '所属组织ID'")
    private Organization organization;

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

    @Column(name = "end_date", nullable = false, columnDefinition = "DATE COMMENT '服务结束日期'")
    private LocalDate endDate;
}
