# Repository Guidelines

## 项目结构与模块组织
- `src/main/java/me/ningyu/app/shuttle/`：Spring Boot 应用主目录
  - `domain/entity/`：JPA 实体（Organization, Line, Route, Stop, StopArea, Trip, Vehicle, Driver 等）
  - `domain/repo/`：Spring Data JPA 仓储接口
  - `facade/controller/`：REST 控制器
  - `facade/service/`：业务逻辑和校验
  - `model/`：DTO 和请求/响应对象
  - `config/`：Spring 配置
  - `enums/`：枚举类
  - `exception/`：自定义异常
- `src/main/resources/`：配置文件（application.yml）
- `src/test/java/`：测试代码

## 构建、测试与本地运行
- `mvn clean install`：编译并运行测试（需 JDK 21）
- `mvn test`：运行全量单元测试
- `mvn spring-boot:run`：本地启动 Spring Boot 应用
- `mvn spring-boot:run -Dspring-boot.run.profiles=local`：使用 local profile 启动

## 编码风格与命名约定
- Java 缩进 4 空格，左大括号同行，导入按 IDE 默认组织
- 包名遵循 `me.ningyu.app.shuttle...` 全小写
- 类名模式：`*Controller`、`*Service`、`*Request`、`*Repository`
- 使用 Lombok 简化样板代码，公共 API 保持清晰可读
- JPA 实体包含注解、中文注释

## 测试规范
- 使用 JUnit 5
- 测试类以 `Tests` 结尾（例：`ApplicationTests`）
- 当前无覆盖率门槛，新增核心逻辑需补测试

## 提交与合并请求
- 历史提交多为简短单行描述，保持简洁明了即可
- PR 需说明：目的、变更范围、验证方式

## 配置与安全
- `application.yml` 包含数据库连接配置
- 避免提交真实凭据与环境密钥
- 本地开发使用 `application-local.yml`（已加入 .gitignore）
