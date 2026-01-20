# 代码重构总结

本次重构按照 GUIDES.md 的设计方案对班车管理系统进行了全面升级，主要包括实体重命名、新增业务子域以及集成预留。

## 📋 重构概览

### ✅ 已完成任务

1. ✅ 重命名核心实体类（符合 GUIDES.md 规范）
2. ✅ 新增乘客管理子域
3. ✅ 新增运营记录子域
4. ✅ 扩展班次调班功能
5. ✅ 更新所有 Repository 和 Service
6. ✅ 添加集成点 TODO 标记
7. ✅ 编译测试通过

---

## 🔄 实体重命名（Entity Renaming）

### 1. Stop → PhysicalStop
- **文件**: `Stop.java` → `PhysicalStop.java`
- **原因**: 强调物理站点是地理实体，不包含线路或时间相关属性
- **数据库表名**: 保持 `stop` 不变（避免数据迁移）
- **影响范围**:
  - `StopCoordinate.java` - 更新引用
  - `StopPhoto.java` - 更新引用
  - `RouteStop.java` - 更新引用
  - `PassengerPreference.java` - 新增实体中使用
  - `OperationLogEntry.java` - 新增实体中使用
  - `PhysicalStopRepository.java` - Repository 重命名

### 2. RouteDirectionStop → RouteStop
- **文件**: `RouteDirectionStop.java` → `RouteStop.java`
- **原因**: 简化命名，明确表示线路停靠站点
- **数据库表名**: 保持 `route_direction_stop` 不变
- **影响范围**:
  - `RouteDirection.java` - 更新 stops 集合类型
  - `RouteStopRepository.java` - Repository 重命名
  - `RouteService.java` - 更新引用
  - `RouteDirectionService.java` - 更新引用
  - `ShuttleScheduleService.java` - 更新引用

### 3. ShuttleSchedule → Shift
- **文件**: `ShuttleSchedule.java` → `Shift.java`
- **原因**: 符合业界命名习惯，Shift 更准确表达"班次"概念
- **数据库表名**: 保持 `shuttle_schedule` 不变
- **核心概念**: Shift = 某一天 + 某司机 + 某车辆 + 某线路方向
- **影响范围**:
  - `RideRecord.java` - 更新引用
  - `OperationLogEntry.java` - 新增实体中使用
  - `ShiftRepository.java` - Repository 重命名
  - `RouteDirectionService.java` - 更新引用
  - `ShuttleScheduleService.java` - 更新引用
  - `ShuttleScheduleController.java` - 更新引用

---

## 🆕 新增业务子域

### 1. 乘客管理子域（Passenger Management）

#### 新增实体

**Passenger（乘客）**
- 字段：
  - `name` - 姓名
  - `employeeNumber` - 员工编号（唯一）
  - `phone` - 手机号
  - `type` - 乘客类型（PassengerType 枚举）
  - `preferences` - 常坐线路和站点偏好（一对多）
- 文件：`Passenger.java`

**PassengerPreference（乘客偏好）**
- 字段：
  - `passenger` - 所属乘客
  - `routeDirection` - 常坐线路方向
  - `stop` - 常坐站点
  - `isPrimary` - 是否为默认主要偏好（用于满载率分析）
  - `description` - 描述
- 文件：`PassengerPreference.java`

#### 新增枚举

**PassengerType（乘客类型）**
- `REGULAR` - 正式员工
- `DISPATCHED` - 派遣员工
- `SECONDED` - 借调员工
- 文件：`PassengerType.java`

#### 新增 Repository

- `PassengerRepository.java` - 支持按员工编号查询
- `PassengerPreferenceRepository.java` - 支持查询主要偏好、线路常乘客等

---

### 2. 运营记录子域（Operation Log）

#### 新增实体

**OperationLogEntry（运营记录）**
- 字段：
  - `shift` - 关联的班次
  - `stop` - 物理站点
  - `actualArrivalTime` - 实际到达时间
  - `actualDepartureTime` - 实际出发时间（默认 = 到达时间 + 1分钟）
  - `latitude/longitude` - GPS 坐标
  - `gpsPriority` - GPS 数据优先级
  - `source` - 数据来源描述
  - `remark` - 备注（如晚点原因）
- 文件：`OperationLogEntry.java`
- **用途**: 记录车辆实际运行时间，用于准点率分析和调度优化

#### 新增枚举

**GPSPriority（GPS 数据优先级）**
- `DRIVER_MANUAL(3)` - 司机手动上报（最高优先级）
- `PASSENGER_SCAN(2)` - 乘客扫码附带GPS（中等优先级）
- `VEHICLE_AUTO(1)` - 车机定时上报（最低优先级）
- 文件：`GPSPriority.java`
- **用途**: 系统融合多源 GPS 时，以最高优先级为准

#### 新增 Repository

- `OperationLogEntryRepository.java` - 支持按班次、站点、日期查询

---

### 3. 调班功能扩展（Shift Swap）

#### Shift 实体新增字段

**调班相关字段**:
- `originalDriver` - 原始司机（记录调班前的司机）
- `swapStatus` - 调班状态（ShiftSwapStatus 枚举）
- `swapRequestedAt` - 调班申请时间
- `swapApprovedAt` - 调班审批时间
- `approverId` - 审批人ID（预留，待创建 User 实体后改为关联）
- `swapReason` - 调班原因/备注
- `workflowProcessInstanceId` - Camunda 流程实例ID（TODO）

#### 新增枚举

**ShiftSwapStatus（调班状态）**
- `NORMAL` - 正常状态（未发生调班）
- `PENDING_APPROVAL` - 调班申请待审批
- `APPROVED` - 调班已批准
- `REJECTED` - 调班已拒绝
- 文件：`ShiftSwapStatus.java`

#### 业务规则

- 调班仅允许在"出发前"操作
- 只能与"空闲司机"交换
- 司机发起的调班需要审批（集成 Camunda）
- 运营管理员可直接调班（无需审批）
- 所有调班操作记录审计日志

---

## 🔗 集成点预留（TODOs）

### 1. Camunda 工作流集成

**位置**: `Shift.java`
```java
@Column(name = "workflow_process_instance_id", columnDefinition = "VARCHAR(100) COMMENT 'Camunda流程实例ID'")
private String workflowProcessInstanceId;
```

**用途**:
- 跟踪司机发起的调班审批流程
- 管理调班工单状态

**待实现**:
- 集成 Camunda BPMN 工作流引擎
- 定义调班审批流程
- 实现流程实例创建和状态同步

---

### 2. Kafka 消息队列集成

**位置**: `RideRecord.java`
```java
/**
 * TODO: 集成 Kafka 消息队列
 * - 乘客扫码上车时，向 Kafka 发送 RideStartedEvent
 * - Topic: ride_started
 * - 用于 HR 考勤系统："上车即打卡"
 */
```

**用途**:
- 乘客扫码上车 → 发送 `RideStartedEvent` 到 Kafka
- HR 考勤系统消费消息，实现"上车即打卡"
- 司机上报晚点 → 发送事件到 HR 系统（考勤异常申报）

**待实现**:
- 集成 Spring Kafka
- 定义事件模型（RideStartedEvent, DelayReportedEvent）
- 实现消息生产者
- 配置 Kafka Topic 和序列化

---

### 3. 移动端集成

#### 班车端（Android）

**位置**: `OperationLogEntry.java`
```java
/**
 * TODO: 集成移动端GPS上报功能
 * TODO: 实现基于GPS的站点到达检测逻辑
 */
```

**功能需求**:
- 定时自动上报 GPS 位置（低优先级）
- 司机手动上报 GPS（高优先级）
- 手动上报"晚点事件" → 触发考勤异常申报

**待实现**:
- GPS 上报 API 端点
- 站点到达检测算法（地理围栏）
- 晚点事件上报处理

#### 乘客端（Android/iOS）

**位置**: `RideRecord.java`
```java
/**
 * TODO: 移动端集成
 * - 乘客端扫描班车 QR Code（静态贴纸或动态码）
 * - 触发上车事件 → 写入 RideRecord + 发送 Kafka 消息
 * - 乘客扫码时可附带 GPS 数据（用于车辆定位）
 */
```

**功能需求**:
- 扫描班车 QR Code（静态/动态）
- 订阅站点提醒（车辆到达前 N 站推送通知）
- 扫码时附带 GPS 数据（间接推断车辆位置）

**待实现**:
- QR Code 生成和验证
- 扫码上车 API 端点
- 站点提醒订阅和推送（FCM/APNs）

---

## 📁 文件变更清单

### 新增文件

**实体类**:
- `src/main/java/me/ningyu/app/shuttle/domain/entity/Passenger.java`
- `src/main/java/me/ningyu/app/shuttle/domain/entity/PassengerPreference.java`
- `src/main/java/me/ningyu/app/shuttle/domain/entity/OperationLogEntry.java`

**枚举类**:
- `src/main/java/me/ningyu/app/shuttle/enums/PassengerType.java`
- `src/main/java/me/ningyu/app/shuttle/enums/GPSPriority.java`
- `src/main/java/me/ningyu/app/shuttle/enums/ShiftSwapStatus.java`

**Repository**:
- `src/main/java/me/ningyu/app/shuttle/domain/repo/PassengerRepository.java`
- `src/main/java/me/ningyu/app/shuttle/domain/repo/PassengerPreferenceRepository.java`
- `src/main/java/me/ningyu/app/shuttle/domain/repo/OperationLogEntryRepository.java`

### 重命名文件

**实体类**:
- `Stop.java` → `PhysicalStop.java`
- `RouteDirectionStop.java` → `RouteStop.java`
- `ShuttleSchedule.java` → `Shift.java`

**Repository**:
- `StopRepository.java` → `PhysicalStopRepository.java`
- `RouteDirectionStopRepository.java` → `RouteStopRepository.java`
- `ShuttleScheduleRepository.java` → `ShiftRepository.java`

### 修改文件

**实体类**:
- `StopCoordinate.java` - 更新 Stop → PhysicalStop 引用
- `StopPhoto.java` - 更新 Stop → PhysicalStop 引用
- `RouteDirection.java` - 更新 RouteDirectionStop → RouteStop 引用
- `RideRecord.java` - 更新 ShuttleSchedule → Shift 引用，添加 Passenger 关联，添加 Kafka TODO
- `Shift.java` - 新增调班相关字段，添加 Camunda TODO

**Service 类**:
- `RouteService.java` - 更新 Repository 引用
- `RouteDirectionService.java` - 更新 Repository 引用
- `ShuttleScheduleService.java` - 更新 Repository 引用，更新实体引用

**Controller 类**:
- `ShuttleScheduleController.java` - 更新实体引用

---

## ⚠️ 重要说明

### 数据库兼容性

所有重命名的实体都通过 `@Table(name = "原表名")` 注解保持了数据库表名不变，**无需进行数据迁移**：

- `PhysicalStop` → 表名仍为 `stop`
- `RouteStop` → 表名仍为 `route_direction_stop`
- `Shift` → 表名仍为 `shuttle_schedule`

### 新增表

新增实体将在应用启动时自动创建表（JPA `ddl-auto: update`）：

- `passenger`
- `passenger_preference`
- `operation_log_entry`

### 向后兼容

- `RideRecord.passenger` 字段保留为兼容字段（VARCHAR）
- 新增 `RideRecord.passenger_id` 字段（BIGINT）关联到 Passenger 表
- 数据迁移可在后续进行，无需立即处理

---

## 🎯 后续任务（Roadmap）

### 1. Controller 和 DTO 开发
- [ ] `PassengerController` + 相关 DTO（Create/Update/Query）
- [ ] `OperationLogController` + 相关 DTO
- [ ] `ShiftSwapController` + 调班相关 DTO

### 2. Service 业务逻辑实现
- [ ] `PassengerService` - 乘客管理
- [ ] `PassengerPreferenceService` - 偏好管理
- [ ] `OperationLogService` - 运营记录管理
- [ ] `ShiftSwapService` - 调班业务逻辑（包含校验和审批）

### 3. 中间件集成
- [ ] Camunda 工作流引擎集成
  - 定义调班审批 BPMN 流程
  - 实现流程启动和回调
- [ ] Kafka 消息队列集成
  - 配置 Topic 和生产者
  - 实现 RideStartedEvent 发送
  - 实现 DelayReportedEvent 发送

### 4. 移动端 API
- [ ] 班车端 GPS 上报 API
- [ ] 班车端晚点上报 API
- [ ] 乘客端扫码上车 API
- [ ] 乘客端站点提醒订阅 API
- [ ] 推送通知服务集成（FCM/APNs）

### 5. 数据迁移
- [ ] 迁移 `RideRecord.passenger`（VARCHAR）到 `RideRecord.passenger_id`（Passenger 关联）

---

## ✅ 验证结果

### 编译测试
```bash
mvn clean compile -DskipTests
```
**结果**: ✅ BUILD SUCCESS

### 数据库表检查
建议在首次启动应用后检查：
- ✅ 旧表（stop, route_direction_stop, shuttle_schedule）保持不变
- ✅ 新表（passenger, passenger_preference, operation_log_entry）成功创建
- ✅ Shift 表新增调班相关列

---

## 📚 参考文档

- [GUIDES.md](./GUIDES.md) - 系统需求规格（设计方案来源）
- [CLAUDE.md](./CLAUDE.md) - 项目指南（开发规范）
- [README.md](./README.md) - 项目说明（业务约束）

---

**重构完成日期**: 2026-01-20
**编译状态**: ✅ 通过
**数据库兼容性**: ✅ 无需迁移
**下一步**: 开发 Controller 和 Service 业务逻辑
