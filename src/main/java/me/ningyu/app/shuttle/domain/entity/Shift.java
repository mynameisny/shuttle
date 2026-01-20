package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.ShiftSwapStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <pre>
 * 排班调度(班次)
 * 某个司机在某一天的排班是开某个线路的某辆车
 * 按照 GUIDES.md 规范，原 ShuttleSchedule 类重命名为 Shift
 * Shift = 某一天 + 某司机 + 某车辆 + 某线路方向
 * </pre>
 */
@Entity(name = "shift")
@Table(name = "shuttle_schedule")  // 保持数据库表名不变,避免数据迁移
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift extends AbstractAuditable
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, columnDefinition = "BIGINT COMMENT '司机ID'")
    private Driver driver;

    @Column(name = "operating_date", nullable = false, columnDefinition = "DATE COMMENT '运营日期，如 2026-01-15'")
    private LocalDate operatingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_direction_id", nullable = false, columnDefinition = "BIGINT COMMENT '带方向的线路ID'")
    private RouteDirection routeDirection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, columnDefinition = "BIGINT COMMENT '车辆ID'")
    private Vehicle vehicle;

    @Column(name = "departure_time", nullable = false, columnDefinition = "TIME COMMENT '发车时间'")
    private LocalTime departureTime;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否启用'")
    private Boolean active = true;

    // ==================== 调班相关字段 ====================

    /**
     * 原始司机ID
     * 用于记录调班前的司机，方便追溯调班历史
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_driver_id", columnDefinition = "BIGINT COMMENT '原始司机ID'")
    private Driver originalDriver;

    /**
     * 调班状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "swap_status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'NORMAL' COMMENT '调班状态'")
    private ShiftSwapStatus swapStatus = ShiftSwapStatus.NORMAL;

    /**
     * 调班申请时间
     */
    @Column(name = "swap_requested_at", columnDefinition = "DATETIME COMMENT '调班申请时间'")
    private LocalDateTime swapRequestedAt;

    /**
     * 调班审批时间
     */
    @Column(name = "swap_approved_at", columnDefinition = "DATETIME COMMENT '调班审批时间'")
    private LocalDateTime swapApprovedAt;

    /**
     * 审批人ID（预留，用于记录运营管理员）
     * TODO: 创建 User 实体后，改为 ManyToOne 关联
     */
    @Column(name = "approver_id", columnDefinition = "BIGINT COMMENT '审批人ID'")
    private Long approverId;

    /**
     * 调班原因/备注
     */
    @Column(name = "swap_reason", columnDefinition = "TEXT COMMENT '调班原因/备注'")
    private String swapReason;

    /**
     * Camunda 工作流流程实例ID
     * TODO: 集成 Camunda 工作流引擎
     * 用于跟踪司机发起的调班审批流程
     */
    @Column(name = "workflow_process_instance_id", columnDefinition = "VARCHAR(100) COMMENT 'Camunda流程实例ID'")
    private String workflowProcessInstanceId;
}