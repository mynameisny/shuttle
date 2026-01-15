# Repository Guidelines

## 项目结构与模块组织
- `app/`：Spring Boot 应用层，包含 controller/service/listener/facade；资源在 `app/src/main/resources`，模板在 `app/src/main/resources/templates`。
- `domain/`：领域模型、命令、事件与仓储接口，位于 `domain/src/main/java/...`。
- `docs/`：接口与 ACL 相关文档。
- `gradle/`、`gradlew`、`settings.gradle.kts`：Gradle 构建配置。
- `Dockerfile`：容器构建入口。
- 测试：当前主要在 `app/src/test/java`，需要时在 `domain/src/test/java` 补充。

## 构建、测试与本地运行
- `./gradlew build`：编译所有模块并运行测试（需 JDK 21）。
- `./gradlew test`：运行全量单元测试。
- `./gradlew :app:bootRun`：本地启动 Spring Boot 应用。
- `./gradlew :domain:build`：仅构建 domain 模块。

## 编码风格与命名约定
- Java 缩进 4 空格，左大括号同行，导入按 IDE 默认组织。
- 包名遵循 `com.cnpc.ucmp.demo...` 全小写。
- 类名模式：`*Controller`、`*Service`、`*Dto`、`*Cmd`、`*Event`、`*Repository`。
- 使用 Lombok 简化样板代码，公共 API 保持清晰可读。
- 未配置格式化/静态检查工具，尽量保持与现有代码一致。
- JPA实体包含注解、中文注释

## 测试规范
- 使用 JUnit 5（`useJUnitPlatform()`）。
- 测试类以 `Test` 结尾（例：`ApplicationTest`）。
- 迭代时优先运行模块级测试：`:app:test` 或 `:domain:test`。
- 当前无覆盖率门槛，新增核心逻辑需补测试。

## 提交与合并请求
- 历史提交多为简短单行描述，保持简洁明了即可。
- PR 需说明：目的、变更范围、验证方式、是否涉及配置/Nacos。
- 关联需求或问题单，变更接口或行为时同步更新 `docs/`。

## 配置与安全
- `app/src/main/resources/application.yml` 引用 Nacos 配置，默认 profile 为 `local`。
- 避免提交真实凭据与环境密钥；需要的配置请写入 `docs/` 并在 PR 中标注。
