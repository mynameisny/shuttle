# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在此代码仓库中工作的指导。

## 项目概述

Shuttle 是一个基于 Spring Boot 4.0.1 和 Java 21 构建的班车调度管理系统。它管理线路、站点、车辆、司机以及乘客乘车记录等班车运营相关的业务。

## 构建与开发命令

### Maven 命令
- `mvn clean install` - 构建项目
- `mvn spring-boot:run` - 本地运行应用
- `mvn test` - 运行所有测试
- `mvn test -Dtest=ClassName` - 运行单个测试类
- `mvn test -Dtest=ClassName#methodName` - 运行单个测试方法

### 应用访问
- 应用运行在 8080 端口
- Swagger UI: http://localhost:8080/swagger-ui.html
- API 文档: http://localhost:8080/api-docs
- 所有 API 请求需要携带请求头: `X-API-Version: v1` (由 ApiVersionInterceptor 强制校验)

## 核心领域架构

### 实体关系模型

系统遵循领域驱动设计，职责边界清晰：

**线路体系**
- `Route` - 线路模板（如"霍营3线"），包含线路名称、管理员等基础信息
- `RouteDirection` - 带方向的线路（上行/下行/环线），包含唯一编码
- `RouteDirectionStop` - 线路方向上的有序停靠站点，包含到达/出发时间

**物理实体**
- `Stop` - 物理站点位置（地理实体），不应包含与线路或时间相关的属性
- `StopCoordinate` - 不同坐标系下的 GPS 坐标（WGS84, GCJ02, BD09）
- `StopPhoto` - 站点实拍照片
- `Vehicle` - 车辆，包含品牌、车型、车牌号、座位数、供应商等信息
- `Driver` - 司机，包含个人信息、联系方式、所属公司、驾驶证

**调度与运营**
- `ShuttleSchedule` - 每日排班：司机 + 车辆 + 线路方向 + 发车时间
  - 核心原则：司机不与车辆或线路永久绑定
  - 按天排班以应对不同的工作日程
- `RideRecord` - 乘客乘车记录，关联到具体的班次

### 关键业务规则（Service 层校验）

来自 README.md，以下约束必须在 Service 层强制执行：
1. 同一时间，司机不能开两辆车
2. 同一时间，车辆不能跑两条线
3. 班次发车时间不能早于线路方向中首站发车时间
4. Stop 实体应仅包含地理数据，不应包含线路相关的时间属性（时间属性归属于 RouteDirectionStop）

### 核心设计模式

**解耦策略**
- 司机通过 `ShuttleSchedule` 与车辆和线路解耦
- 直接"司机-线路"绑定缺少时间上下文（哪一天、哪个方向）
- 排班的最小单位是每天：具体日期 + 线路方向 + 司机 + 车辆

**审计功能**
- 所有实体继承 `AbstractAuditable`（通过 Application.java 中的 `@EnableJpaAuditing` 启用）
- 自动提供 createdAt、updatedAt、createdBy、updatedBy 字段跟踪

**API 版本控制**
- 所有接口端点都需要 `X-API-Version: v1` 请求头
- 由 `ApiVersionInterceptor` 拦截器强制执行
- 缺失或无效的版本号会返回 400 错误和 JSON 错误信息

## 包结构

```
me.ningyu.app.shuttle/
├── domain/
│   ├── entity/          # JPA 实体，包含注解和中文注释
│   └── repo/            # Spring Data JPA 仓储接口
├── facade/
│   ├── controller/      # REST 控制器
│   └── service/         # 业务逻辑和校验
├── model/               # DTO 和请求/响应对象
├── config/              # Spring 配置（WebConfig, SpringDocConfig, 拦截器）
├── enums/               # 枚举类（Direction, Gender, CoordinateSystem）
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
本地开发使用 `application-local.yml` 配置文件（已在 .gitignore 中排除）：

1. 创建本地数据库：
   ```sql
   CREATE DATABASE shuttle CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 复制配置模板并修改连接信息：
   - 数据库地址：根据本地 MySQL 配置修改
   - 用户名/密码：根据本地环境配置

3. 启动应用时指定 local profile：
   ```bash
   # 使用 Maven
   mvn spring-boot:run -Dspring-boot.run.profiles=local

   # 或在 IDEA 中设置 Active profiles: local
   ```

4. 配置特性：
   - 启用 SQL 格式化（format_sql: true）便于调试
   - 关闭 SSL 连接
   - 使用 Asia/Shanghai 时区

## 代码风格

- 使用 Lombok 注解（@Getter, @Setter, @Builder, @NoArgsConstructor, @AllArgsConstructor）
- JPA 实体的 columnDefinition 包含中文注释说明业务含义
- Repository 接口继承 Spring Data JPA repositories
- Service 使用 `@Transactional` 并通过构造器注入 repositories（使用 Lombok @RequiredArgsConstructor）
- 校验逻辑在 Service 层实现，而不仅仅依赖注解

## 测试

- 测试类位于 `src/test/java/`，使用 JUnit
- 当前测试覆盖率较低
- 测试框架：Spring Boot Test with JPA, Validation, and WebMvc test starters

## 重要说明

- AGENTS.md 是另一个项目的文档（使用 Gradle、不同的包结构）- 请忽略
- 本项目使用 Maven，不是 Gradle
- `ShuttleScheduleService` 中的调度逻辑包含冲突校验和乘车记录检查
- 删除班次要求：不能是过去的日期、不能有现有的乘车记录
- 批量创建排班会生成日期列表，可选择排除周末
