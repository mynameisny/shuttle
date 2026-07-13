# Shuttle 班车服务 - 架构文档

> **版本**: 0.0.1-SNAPSHOT | **生成日期**: 2026-06-29 | **基础框架**: Spring Boot 4.0.1 + Java 21

---

## 1. 概述

Shuttle（班车服务）是一套企业班车调度管理系统，提供路线规划、车辆管理、司机管理、排班调度、乘客偏好管理以及实时运营日志上报等核心能力。系统以 **Spring Boot 单体应用** 形式部署，采用 **DDD 风格的分层架构**，通过 RESTful API 对外提供服务。

### 核心功能域

| 功能域 | 说明 |
|--------|------|
| **路线管理** | 线路模板（Route）→ 带方向线路（RouteDirection）→ 站点序列（RouteStop）三级模型 |
| **排班调度** | 车辆/司机/线路按日期排班，支持冲突检测与调班工作流 |
| **运营日志** | GPS 坐标 + 到达/出发时间上报，支持司机手动、乘客扫码、车机自动三种模式 |
| **乘客管理** | 乘客信息 + 乘车偏好（常坐线路/站点），支撑按线路站点查询常乘客 |
| **司机管理** | 司机个人信息 + 驾照信息 |
| **车辆管理** | 车辆品牌、车牌、座位数等基础信息 |

---

## 2. 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21（编译目标 Java 15） |
| 框架 | Spring Boot | 4.0.1 |
| Web | Spring MVC（spring-boot-starter-webmvc） | — |
| ORM | Spring Data JPA + Hibernate | — |
| 校验 | Bean Validation（spring-boot-starter-validation） | — |
| 数据库 | MySQL | — |
| 连接池 | HikariCP | 默认 |
| API 文档 | SpringDoc OpenAPI 3 | v3.0.1 |
| 监控 | Spring Boot Actuator | — |
| 构建 | Maven | — |
| 容器化 | Docker + Docker Compose | — |
| 工具库 | Lombok | — |

---

## 3. 整体架构

### 3.1 架构模式：DDD 风格分层架构

```
┌─────────────────────────────────────────────┐
│                  façade 层                   │
│       (表示层：REST API 入口)                 │
│  ┌───────────────┐ ┌──────────────────────┐ │
│  │   controller   │ │       service        │ │
│  │  @RestController│ │      @Service        │ │
│  └───────┬───────┘ └──────────┬───────────┘ │
├──────────┼────────────────────┼──────────────┤
│          │         model 层    │              │
│          │     (请求 DTO)      │              │
│          │  CreateXxxRequest   │              │
│          │  UpdateXxxRequest   │              │
│          └──────────┬──────────┘              │
├─────────────────────┼─────────────────────────┤
│                 domain 层                      │
│             (领域模型 + 仓储)                   │
│  ┌───────────────┐ ┌──────────────────────┐   │
│  │    entity     │ │        repo          │   │
│  │   @Entity     │ │ JpaRepository 接口    │   │
│  └───────────────┘ └──────────────────────┘   │
├───────────────────────────────────────────────┤
│           enums / exception / config          │
│              (横切关注点)                       │
└───────────────────────────────────────────────┘
```

### 3.2 调用流程

```
HTTP Request
    │
    ▼
@RestController (façade/controller)     ← 接收请求，参数校验 (@Valid)
    │
    ▼
@Service (façade/service)               ← 业务逻辑编排、领域约束校验
    │
    ▼
Repository (domain/repo)                ← Spring Data JPA 接口
    │
    ▼
@Entity (domain/entity)                 ← JPA 实体，映射数据库表
    │
    ▼
MySQL Database
```

### 3.3 架构特征

- **无显式响应 DTO**：实体直接序列化为 JSON 返回（开发阶段简化策略）
- **无独立映射层**：服务层通过 Builder 模式手动完成 DTO → Entity 转换
- **无安全层**：SpringDoc 文档声明了 `X-Auth-Token` 安全方案，但未强制执行
- **无全局异常处理**：缺少 `@ControllerAdvice`
- **API 版本控制**：通过 `X-API-Version` Header 预留，当前无实际版本选择逻辑
- **JPA 审计**：通过 `@EnableJpaAuditing` + `AbstractAuditable` 自动记录创建/修改时间与操作人

---

## 4. 模块与包结构

```
me.ningyu.app.shuttle/
├── Application.java                     # ★ 应用入口
├── config/                              # Spring 配置
│   ├── SpringDocConfig.java             # OpenAPI/Swagger 配置
│   └── WebConfig.java                   # WebMVC 配置（预留）
├── domain/
│   ├── entity/                          # JPA 实体
│   │   ├── AbstractAuditable.java       # 审计基类（@MappedSuperclass）
│   │   ├── Route.java                   # 线路模板
│   │   ├── RouteDirection.java          # 带方向线路
│   │   ├── RouteStop.java               # 站点序列
│   │   ├── PhysicalStop.java            # 物理站点
│   │   ├── StopCoordinate.java          # 站点坐标
│   │   ├── StopPhoto.java               # 站点照片
│   │   ├── Shift.java                   # 排班/班次
│   │   ├── Driver.java                  # 司机
│   │   ├── DrivingLicense.java          # 驾照
│   │   ├── Vehicle.java                 # 车辆
│   │   ├── Passenger.java               # 乘客
│   │   ├── PassengerPreference.java     # 乘客偏好
│   │   ├── OperationLogEntry.java       # 运营日志
│   │   └── RideRecord.java              # 乘车记录
│   └── repo/                            # Spring Data JPA Repository
│       ├── RouteRepository.java
│       ├── RouteDirectionRepository.java
│       ├── RouteStopRepository.java
│       ├── PhysicalStopRepository.java
│       ├── ShiftRepository.java
│       ├── DriverRepository.java
│       ├── VehicleRepository.java
│       ├── PassengerRepository.java
│       ├── PassengerPreferenceRepository.java
│       ├── OperationLogEntryRepository.java
│       └── RideRepository.java
├── facade/
│   ├── controller/                      # REST 控制器
│   │   ├── RouteController.java         # /api/routes
│   │   ├── RouteDirectionController.java # /api/route-directions
│   │   ├── ShuttleScheduleController.java # /api/schedules
│   │   ├── OperationLogController.java  # /api/operation-logs
│   │   ├── PassengerController.java     # /api/passengers
│   │   └── StopController.java          # 站点管理（空占位）
│   └── service/                         # 应用/领域服务
│       ├── RouteService.java
│       ├── RouteDirectionService.java
│       ├── ShuttleScheduleService.java
│       ├── OperationLogService.java
│       └── PassengerService.java
├── model/                               # 请求 DTO
│   ├── route/
│   │   ├── CreateRouteRequest.java
│   │   ├── UpdateRouteRequest.java
│   │   ├── CreateRouteDirectionRequest.java
│   │   └── UpdateRouteDirectionRequest.java
│   ├── schedule/
│   │   ├── CreateScheduleRequest.java
│   │   └── UpdateScheduleRequest.java
│   ├── shift/
│   │   ├── ShiftSwapRequest.java
│   │   └── ApproveShiftSwapRequest.java
│   ├── operation/
│   │   ├── CreateOperationLogRequest.java
│   │   └── UpdateOperationLogRequest.java
│   └── passenger/
│       ├── CreatePassengerRequest.java
│       ├── UpdatePassengerRequest.java
│       └── CreatePassengerPreferenceRequest.java
├── enums/                               # 枚举
│   ├── Direction.java                   # 线路方向
│   ├── CoordinateSystem.java            # 坐标系统
│   ├── Gender.java                      # 性别
│   ├── GPSPriority.java                 # GPS 上报优先级
│   ├── PassengerType.java               # 乘客类型
│   └── ShiftSwapStatus.java             # 调班状态
└── exception/
    └── ValidationException.java         # 校验异常（占位）
```

---

## 5. 领域模型

### 5.1 实体关系图

```
Route (线路模板)
  │ 1
  │
  ├─── RouteDirection (带方向线路)
  │      │ 1
  │      │
  │      ├─── RouteStop (站点序列) ──── PhysicalStop (物理站点)
  │      │                                   │ 1
  │      │                                   ├─── StopCoordinate (站点坐标)
  │      │                                   └─── StopPhoto (站点照片)
  │      │
  │      ├─── Shift (排班)
  │      │     ├─── Driver (司机) ──── DrivingLicense (驾照)
  │      │     ├─── Vehicle (车辆)
  │      │     ├─── OperationLogEntry (运营日志) ──── PhysicalStop
  │      │     └─── RideRecord (乘车记录) ──── Passenger (乘客)
  │      │
  │      └─── PassengerPreference (乘客偏好) ──── Passenger
  │                                               │
  │                                               └─── PhysicalStop
```

### 5.2 实体清单

| 实体 | 数据库表 | 核心字段 |
|------|---------|---------|
| **Route** | `route` | id, name, manager, sequence, direction |
| **RouteDirection** | `route_direction` | id, code, route_id(FK), direction |
| **RouteStop** | `route_direction_stop` | id, routeDirection_id(FK), physicalStop_id(FK), sequence, arrivalTime, departureTime |
| **PhysicalStop** | `stop` | id, name, description, sequence, latitude, longitude, coordinateSystem |
| **StopCoordinate** | `stop_coordinate` | id, stop_id(FK), latitude, longitude, coordinateSystem |
| **StopPhoto** | `stop_photo` | id, stop_id(FK), filename, storagePath, publicUrl |
| **Shift** | `shuttle_schedule` | id, routeDirection_id(FK), driver_id(FK), vehicle_id(FK), operatingDate, departureTime, active, swapStatus, originalDriver, swapReason |
| **Driver** | `driver` | id, name, gender, idCard, phone, email, company |
| **DrivingLicense** | `driving_license` | id, driver_id(FK), licenseClass, licenseNumber, birthDate, dateOfFirstIssue, issueTime, expireTime |
| **Vehicle** | `vehicle` | id, brand, series, model, type, plateNumber, seats, vendor |
| **Passenger** | `passenger` | id, name, employeeNumber, phone, type, department |
| **PassengerPreference** | `passenger_preference` | id, passenger_id(FK), routeDirection_id(FK), stop_id(FK), primary |
| **OperationLogEntry** | `operation_log_entry` | id, shift_id(FK), stop_id(FK), actualArrivalTime, actualDepartureTime, latitude, longitude, gpsPriority, source |
| **RideRecord** | `ride_record` | id, passenger_id(FK), shift_id(FK), passengerName, boardTime, alightTime |

### 5.3 审计基类 AbstractAuditable

所有实体继承 `AbstractAuditable`（`@MappedSuperclass`），自动继承以下审计字段：

| 字段 | 类型 | 注解 | 说明 |
|------|------|------|------|
| `id` | `Long` | `@Id @GeneratedValue(strategy = IDENTITY)` | 自增主键 |
| `createdBy` | `String` | — | 创建人 |
| `createdDate` | `LocalDateTime` | `@CreatedDate` | 创建时间 |
| `lastModifiedBy` | `String` | `@LastModifiedBy` | 最后修改人 |
| `lastModifiedDate` | `LocalDateTime` | `@LastModifiedDate` | 最后修改时间 |

---

## 6. REST API 端点

### 6.1 路线管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/routes` | 创建线路模板 |
| `PUT` | `/api/routes/{id}` | 更新线路模板 |
| `DELETE` | `/api/routes/{id}` | 删除线路模板 |
| `GET` | `/api/routes/{id}` | 获取线路详情 |
| `GET` | `/api/routes` | 获取所有线路 |

### 6.2 带方向线路管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/route-directions` | 创建带方向线路 |
| `PUT` | `/api/route-directions/{id}` | 更新带方向线路 |
| `DELETE` | `/api/route-directions/{id}` | 删除带方向线路 |
| `GET` | `/api/route-directions/{id}` | 获取带方向线路详情 |
| `GET` | `/api/route-directions` | 获取所有带方向线路 |
| `GET` | `/api/route-directions/by-route/{routeId}` | 按线路模板查带方向线路 |

### 6.3 排班管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/schedules` | 批量创建排班 |
| `PUT` | `/api/schedules/{id}` | 编辑单个排班 |
| `DELETE` | `/api/schedules/{id}` | 取消排班 |
| `POST` | `/api/schedules/shift-swap` | 申请调班 |
| `POST` | `/api/schedules/shift-swap/approve` | 审批调班申请 |
| `GET` | `/api/schedules/shift-swap/pending` | 查询待审批调班 |

### 6.4 运营日志

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/operation-logs` | 创建运营记录 |
| `PUT` | `/api/operation-logs/{id}` | 更新运营记录 |
| `DELETE` | `/api/operation-logs/{id}` | 删除运营记录 |
| `GET` | `/api/operation-logs/{id}` | 获取运营记录详情 |
| `GET` | `/api/operation-logs/by-shift/{shiftId}` | 按班次查询运营记录 |
| `GET` | `/api/operation-logs/by-shift-and-stop` | 按班次+站点查询 |
| `GET` | `/api/operation-logs/by-date/{date}` | 按日期查询运营记录 |

### 6.5 乘客管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/passengers` | 创建乘客 |
| `PUT` | `/api/passengers/{id}` | 更新乘客 |
| `DELETE` | `/api/passengers/{id}` | 删除乘客 |
| `GET` | `/api/passengers/{id}` | 获取乘客详情 |
| `GET` | `/api/passengers` | 获取所有乘客 |
| `GET` | `/api/passengers/by-employee-number/{employeeNumber}` | 按员工编号查询 |
| `POST` | `/api/passengers/preferences` | 添加乘客偏好 |
| `GET` | `/api/passengers/{passengerId}/preferences` | 获取乘客所有偏好 |
| `GET` | `/api/passengers/{passengerId}/preferences/primary` | 获取主要偏好 |
| `DELETE` | `/api/passengers/preferences/{preferenceId}` | 删除偏好 |
| `PUT` | `/api/passengers/preferences/{preferenceId}/set-primary` | 设置为主要偏好 |
| `GET` | `/api/passengers/by-route-direction/{routeDirectionId}` | 按线路方向查常乘客 |
| `GET` | `/api/passengers/by-stop/{stopId}` | 按站点查常乘客 |

---

## 7. 核心业务逻辑

### 7.1 排班冲突检测

`ShuttleScheduleService` 在创建排班时进行司机与车辆的双重冲突检测：

- **检测窗口**：排班时间前后 **3 小时** 不能有重叠排班
- **检测维度**：
  - 同一司机在冲突窗口内是否已有排班
  - 同一车辆在冲突窗口内是否已有排班
- **实现方式**：`ShiftRepository` 中通过原生 SQL `INTERVAL 3 HOUR` 进行时间窗口重叠计算
- **异常处理**：冲突时抛出 `IllegalStateException`

### 7.2 调班工作流

调班流程支持两种模式，通过 `ShiftSwapStatus` 枚举管理状态：

| 状态 | 说明 |
|------|------|
| `NORMAL` | 正常排班，无调班 |
| `PENDING_APPROVAL` | 司机发起调班，等待管理员审批 |
| `APPROVED` | 管理员批准调班 |
| `REJECTED` | 管理员拒绝调班 |

**调班发起**（`POST /api/schedules/shift-swap`）：
1. 验证排班与新司机是否存在
2. 验证新司机在相同时段无冲突
3. **管理员直调**：直接执行调班
4. **司机申请**：将 `swapStatus` 设为 `PENDING_APPROVAL`，记录 `originalDriver`、`swapReason`、`swapRequestedAt`
5. 预留 Camunda 工作流集成（`workflowProcessInstanceId` 字段）

### 7.3 GPS 优先级机制

运营日志支持三种 GPS 上报来源，按 `GPSPriority` 区分可信度：

| 优先级 | 来源 | 优先级值 |
|--------|------|---------|
| `VEHICLE_AUTO` | 车机自动上报 | 1 |
| `PASSENGER_SCAN` | 乘客扫码上车 | 2 |
| `DRIVER_MANUAL` | 司机手动记录 | 3 |

- 高优先级数据可覆盖低优先级数据（通过 `isHigherThan()` 方法比较）
- 建议实施方案：Kafka 消息队列接收三种来源数据，按优先级合并写入

### 7.4 到达/出发时间验证

`OperationLogService` 对运营日志实施时间约束：
- `actualArrivalTime`：必填字段
- `actualDepartureTime`：可选
- 如同时提供：`actualDepartureTime` 不得早于 `actualArrivalTime`

---

## 8. 枚举定义

| 枚举 | 值 | 序列化方式 |
|------|-----|-----------|
| `Direction` | `UPSTREAM("上行", "只到公司")`, `DOWNSTREAM("下行", "只从公司出发")`, `LOOP("环线", "往返")` | 自定义 `@JsonCreator/@JsonValue` |
| `CoordinateSystem` | `WGS84`, `GCJ02`, `BD09`, `CGCS2000`, `UNKNOWN` | 自定义 `@JsonCreator/@JsonValue` |
| `Gender` | `MALE("男")`, `FEMALE("女")` | JPA `@Enumerated(STRING)` |
| `GPSPriority` | `DRIVER_MANUAL(3)`, `PASSENGER_SCAN(2)`, `VEHICLE_AUTO(1)` | JPA `@Enumerated(ORDINAL)` |
| `PassengerType` | `REGULAR("正式")`, `DISPATCHED("派遣")`, `SECONDED("借调")` | JPA `@Enumerated(STRING)` |
| `ShiftSwapStatus` | `NORMAL`, `PENDING_APPROVAL`, `APPROVED`, `REJECTED` | JPA `@Enumerated(STRING)` |

---

## 9. 数据库配置

### 9.1 配置概览

```yaml
# application.yml (生产/开发环境)
spring.datasource:
  url: jdbc:mysql://svc-mysql.ucmp-ctl.svc.cluster.local:3001/ucmp_shuttle
  username: ucmp_dev
spring.jpa.hibernate.ddl-auto: update    # 自动创建/更新表结构
spring.jpa.show-sql: true

# application-local.yml (本地开发)
spring.datasource:
  url: jdbc:mysql://192.168.5.251:3306/shuttle
  username: root
spring.jpa.hibernate.ddl-auto: update
spring.jpa.properties.hibernate.format_sql: true
```

### 9.2 DDL 策略

当前使用 `ddl-auto: update`（Hibernate 自动管理表结构），适用于开发阶段。**生产环境建议改为 `validate` 或使用 Flyway/Liquibase 进行数据库迁移管理。**

---

## 10. 配置与服务

| 配置类 | 职责 |
|--------|------|
| `SpringDocConfig` | OpenAPI 3 文档配置：标题"班车服务"，全局安全方案 `X-Auth-Token`（API Key），全局参数 `X-API-Version`，两个服务器环境（localhost:8080 与 Dev），外部文档链接 |
| `WebConfig` | `WebMvcConfigurer` 实现，当前为空（预留拦截器注册） |

### 外部服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Shuttle API | `8080` | 主应用服务 |
| Swagger UI | `8080/swagger-ui.html` | API 文档界面 |
| API Docs | `8080/api-docs` | OpenAPI JSON |
| MySQL | `3306`（本地）/ `3001`（集群） | 数据库服务 |

---

## 11. 已知待办项（TODO）

| 编号 | 内容 | 影响范围 |
|------|------|---------|
| 1 | **GPS 集成**：班车端 Android GPS 上报、乘客端 App 扫码、车机定时上报 | OperationLogEntry |
| 2 | **Kafka 集成**：为 `RideRecord` 发布 `RideStartedEvent`，对接 HR 考勤系统（"上车即打卡"） | RideRecord |
| 3 | **Camunda 工作流**：调班审批流程，关联 `Shift.workflowProcessInstanceId` | Shift |
| 4 | **DrivingLicense 字段修正**：`birthDate`/`dateOfFirstIssue`/`issueTime`/`expireTime` 错误使用了 `LocalTime`，应为 `LocalDate`/`LocalDateTime` | DrivingLicense |
| 5 | **ShiftRepository 补全**：缺少 `findBySwapStatus()` 方法，`getPendingShiftSwaps()` 目前返回空列表 | ShiftRepository |
| 6 | **User 实体**：尚未创建，`Shift.approverId` 字段预留将来用作 `@ManyToOne` 关系 | Shift |
| 7 | **StopController**：存在但完全为空，需要实现站点 CRUD | StopController |
| 8 | **ValidationException**：异常类存在但未被任何地方使用 | 全局异常处理 |
| 9 | **全局异常处理器**：缺少 `@ControllerAdvice`，当前直接抛出 `IllegalArgumentException`/`IllegalStateException` | 所有 Controller |
| 10 | **安全层**：缺少认证/授权机制，SpringDoc 声明的 `X-Auth-Token` 未实现 | 全局 |
| 11 | **API 版本控制**：配置了 `X-API-Version` Header 但无实际路由逻辑 | 所有 API |

---

## 12. 构建与部署

### 12.1 构建

```bash
# 编译 + 测试
./mvnw clean package

# 仅编译，跳过测试
./mvnw clean package -DskipTests

# 运行应用
./mvnw spring-boot:run

# 指定 profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 12.2 Docker Compose（MySQL 依赖）

```yaml
# compose.yaml
services:
  mysql:
    image: mysql:latest
    environment:
      MYSQL_DATABASE: mydatabase
      MYSQL_USER: myuser
      MYSQL_PASSWORD: secret
      MYSQL_ROOT_PASSWORD: rootsecret
    ports:
      - "3306:3306"
```

---

## 13. 架构评估与改进建议

### 当前优势

- 分层清晰，包结构按 DDD 理念组织，职责边界明确
- 所有 Repository 同时扩展 `PagingAndSortingRepository` 与 `JpaSpecificationExecutor`，为复杂查询提供了良好基础设施
- 通过 JPA 审计自动记录创建/修改信息
- API 文档通过 SpringDoc 自动生成

### 改进建议

| 优先级 | 建议 | 说明 |
|--------|------|------|
| **高** | 引入响应 DTO | 实体直接暴露为 JSON 存在过度暴露字段、循环引用风险，建议为每个 API 定义独立响应 DTO |
| **高** | 补全全局异常处理 | 添加 `@ControllerAdvice`，统一处理 `IllegalArgumentException`、`ValidationException` 等，返回标准错误格式 |
| **高** | 数据库迁移方案 | 将 `ddl-auto: update` 替换为 Flyway/Liquibase，杜绝自动 DDL 变更的风险 |
| **中** | 引入安全层 | 实现 `X-Auth-Token` 认证，添加 Spring Security 或自定义 Filter |
| **中** | 消除循环引用 | 实体间的 `@JsonManagedReference/@JsonBackReference` 仅部分使用（仅 DrivingLicense），建议统一 JSON 序列化策略 |
| **中** | 完善 StopController | 当前为空占位符，需实现站点 CRUD |
| **低** | 引入 MapStruct | 替换手动 Builder 转换 DTO ↔ Entity，降低样板代码 |
| **低** | 补充单元测试 | 当前仅有 1 个冒烟测试，需为核心业务逻辑（冲突检测、调班流程）补充测试 |
| **低** | DrivingLicense 字段修正 | `LocalTime` 字段修正为 `LocalDate`/`LocalDateTime` |
