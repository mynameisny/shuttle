# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码仓库中工作的指导。

## 项目概述

Shuttle 是一个通用交通运输系统，参考 GTFS（Google Transit Feed Specification）数据模型设计，支持公交线路、企业班车、园区摆渡车、景区接驳车等多种交通场景。基于 Spring Boot 4.0.1 和 Java 21 构建。

## 构建与开发命令

### Maven 命令
- `mvn clean install` - 构建项目
- `mvn spring-boot:run` - 本地运行应用
- `mvn test` - 运行所有测试
- `mvn test -Dtest=ClassName` - 运行单个测试类

### 应用访问
- 应用运行在 8080 端口
- Swagger UI: http://localhost:8080/swagger-ui.html
- API 文档: http://localhost:8080/api-docs

## 核心领域架构

### 实体关系模型（参考 GTFS）

系统遵循领域驱动设计，参考 GTFS 标准，支持多组织（SaaS）模式：

```
Organization（组织/租户）
│
├── Line（线路）
│      │
│      ├── Route（线路方向）
│      │      │
│      │      └── RouteStop（路线站点）
│      │               │
│      │               ▼
│      │             Stop（停靠点）
│      │               │
│      │               ▼
│      │           StopArea（站点区域）
│      │
│      └── Trip（班次）
│              │
│              ├── Vehicle（车辆）
│              └── Driver（司机）
│
└── VehicleLocation（车辆位置）
```

**核心实体说明**

| 实体 | GTFS 对应 | 说明 |
|------|-----------|------|
| Organization | agency.txt | 交通运营机构（多租户扩展） |
| Line | routes.txt | 业务线路（如"463路"） |
| Route | trips + shapes + direction | 线路方向/变体（如"463去程"） |
| StopArea | stops.txt (parent_station) | 站点名称聚合（用户看到的站名） |
| Stop | stops.txt | 实际停靠点（含GPS坐标） |
| RouteStop | stop_times.txt | 路线上的停靠顺序 |
| Trip | trips.txt | 一次具体运行（日期+时间+车辆+司机） |
| Vehicle | GTFS-Realtime Vehicle | 车辆 |
| VehicleLocation | GTFS-Realtime VehiclePosition | 实时位置 |
| Driver | 无 | 企业运营扩展 |

**关键设计原则**

1. **Line 与 Route 分离**：Line 是业务线路，Route 是具体运行方向
2. **StopArea 与 Stop 分离**：StopArea 是用户看到的站名，Stop 是实际停车位置（含GPS）
3. **Trip 独立建模**：某一天某一时间执行的一次 Route
4. **RouteStop 关联 Stop**：而非 StopArea，确保去程/返程停靠不同位置

**GTFS 扩展实体**
- `ServiceCalendar` - 服务日历（对应 calendar.txt）
- `CalendarDate` - 特殊日期（对应 calendar_dates.txt）
- `Shape` - 线路形状点（对应 shapes.txt）
- `FareAttribute` - 票价属性（对应 fare_attributes.txt）
- `FareRule` - 票价规则（对应 fare_rules.txt）
- `Frequency` - 发车频率（对应 frequencies.txt）
- `Transfer` - 换乘规则（对应 transfers.txt）
- `Pathway` - 站内通道（对应 pathways.txt）
- `Level` - 站内楼层（对应 levels.txt）
- `FeedInfo` - 数据源信息（对应 feed_info.txt）

**辅助实体**
- `Passenger` - 乘客（员工编号、类型、偏好）
- `PassengerPreference` - 乘客偏好（常坐线路方向和站点）
- `RideRecord` - 乘车记录
- `OperationLogEntry` - 运营记录（实际到站/离站时间）
- `StopPhoto` - 站点实拍照片

### 关键业务规则（Service 层校验）

1. 同一时间，司机不能开两辆车
2. 同一时间，车辆不能跑两条线
3. Trip 是系统核心，GPS、到站预测、调度都围绕 Trip 展开
4. Stop 实体包含 GPS 坐标，StopArea 不包含

### 核心设计模式

**审计功能**
- 所有实体继承 `AbstractAuditable`（通过 Application.java 中的 `@EnableJpaAuditing` 启用）
- 自动提供 createdDate、lastModifiedDate、createdBy、lastModifiedBy 字段

## 包结构

```
me.ningyu.app.shuttle/
├── domain/
│   ├── entity/          # JPA 实体（Organization, Line, Route, Stop, StopArea, Trip, Vehicle, Driver 等）
│   └── repo/            # Spring Data JPA 仓储接口
├── facade/
│   ├── controller/      # REST 控制器（LineController, RouteController, TripController 等）
│   └── service/         # 业务逻辑和校验（LineService, RouteService, TripService 等）
├── model/               # DTO 和请求/响应对象
├── config/              # Spring 配置
├── enums/               # 枚举类（Direction, LineStatus, RouteStatus, TripStatus, VehicleStatus, DriverStatus）
└── exception/           # 自定义异常
```

## 技术栈

- Spring Boot 4.0.1 (webmvc, data-jpa, validation, actuator, devtools)
- JPA/Hibernate with MySQL (ddl-auto: update, show-sql: true)
- SpringDoc OpenAPI 3.0.1 用于 API 文档
- Lombok 用于减少样板代码
- Java 21

## 数据库配置

### 生产/测试环境
`application.yml` 中的数据库连接：
- MySQL 集群端点，启用 SSL
- JPA 自动更新模式（ddl-auto: update）
- SQL 日志已启用（show-sql: true）

注意：application.yml 包含真实凭据 - 请勿提交凭据变更。

### 本地开发环境
本地开发使用 `application-local.yml` 配置文件（需添加到 .gitignore）：

1. 创建本地数据库：
   ```sql
   CREATE DATABASE shuttle CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 创建 `src/main/resources/application-local.yml` 文件：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/shuttle?useSSL=false&serverTimezone=Asia/Shanghai
       username: root
       password: your_password
     jpa:
       hibernate:
         ddl-auto: update
       show-sql: true
       properties:
         hibernate:
           format_sql: true
   ```

3. 启动应用时指定 local profile：
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

## 代码风格

- 使用 Lombok 注解（@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor）
- JPA 实体的 columnDefinition 包含中文注释说明业务含义
- Repository 接口继承 Spring Data JPA repositories
- Service 使用 `@Transactional` 并通过构造器注入 repositories（使用 Lombok @RequiredArgsConstructor）
- 校验逻辑在 Service 层实现

## 测试

- 测试类位于 `src/test/java/`，使用 JUnit
- 测试框架：Spring Boot Test with JPA, Validation, and WebMvc test starters

## 重要说明

- AGENTS.md 是另一个项目的文档 - 请忽略
- 本项目使用 Maven，不是 Gradle
- TripService 中的调度逻辑包含冲突校验和乘车记录检查
- 删除班次要求：不能是过去的日期、不能有现有的乘车记录
