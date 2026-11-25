# 事件中心与 CDC 使用文档

> **生成日期**: 2026-11-24 | **实现状态**: Transactional Outbox 已完成；CDC + Kafka 已端到端跑通
> **关联文档**: [架构文档](./arch.md)

---

## 1. 概述

事件中心为班车系统提供「业务事实发布与下游广播」能力，由两部分构成：

| 能力 | 说明 | 状态 |
|------|------|------|
| 事务性 Outbox | 业务数据与事件记录在同一本地事务内原子提交 | ✅ 已完成 |
| CDC + Kafka | Debezium 监听 Outbox 表变更 → 广播到 Kafka → 供下游消费 | ✅ 端到端跑通（Outbox Event Router 待打磨） |

核心设计目标：

- **业务与基础设施解耦**：业务模块只「发布事件」，不感知通知渠道、接收人、消息队列。
- **原子性保证**：事件记录与业务数据同事务提交/回滚，杜绝「业务成功但事件丢失」或「事件发出但业务回滚」。
- **稳定事件编码**：使用 `<domain>.<entity_action>` 字符串（如 `trip.cancelled`），不使用 Java 类全限定名。
- **快照语义**：事件携带业务事实快照，消费方无需回查可能已变化的实体；不含个人敏感信息。

---

## 2. 整体链路

```
业务方法 (@Transactional)
    │
    ├── 修改业务数据（JPA）
    │
    ├── eventPublisher.publish(new XxxEvent(data))
    │        │
    │        ▼
    │   DomainEventPublisherImpl
    │        │  构建 EventEnvelope（eventId/source/time/operator/...）
    │        ▼
    │   Spring 应用内事件广播（同步）
    │        │
    │        ▼
    │   EventBoxListener（同事务）
    │        │  EventSerializer 序列化信封 → 写入 sys_event_box
    │        ▼
    └── 事务提交（业务数据 + sys_event_box 原子提交）───► MySQL

                    （异步，跨进程）

Debezium CDC（binlog 监听 sys_event_box）
    │
    ▼
Kafka Topic: shuttle.ucmp_shuttle.sys_event_box
    │
    ▼
下游消费者 / 通知中心 / 其他系统
```

---

## 3. 包结构与组件职责

代码位于 `me.ningyu.app.shuttle.event`：

```
event/
├── api/                          # 业务模块唯一依赖的契约
│   ├── DomainEvent.java          # 事件接口：getEventType/getEventVersion/getData/getSubject
│   └── DomainEventPublisher.java # 发布器接口：publish / publishAll
├── core/                         # 基础设施
│   ├── EventEnvelope.java        # 统一事件信封（CloudEvents 风格）
│   ├── EventTypes.java           # 事件编码常量
│   ├── EventTypeRegistry.java    # 代码级事件类型注册表（发布前校验）
│   ├── EventSerializer.java      # Jackson + JavaTime 序列化信封为 JSON
│   └── DomainEventPublisherImpl.java # 构建信封 + 同步广播
├── eventbox/                     # Outbox 落库
│   ├── EventBoxRecord.java       # 实体，映射表 sys_event_box
│   ├── EventBoxRecordRepository.java
│   └── EventBoxListener.java     # @EventListener，同事务同步写入
└── domain/                       # 具体事件实现
    ├── TripCancelledEvent.java
    └── DriverStatusChangedEvent.java
```

### 3.1 DomainEvent 契约

```java
public interface DomainEvent {
    String getEventType();       // 稳定事件编码，如 trip.cancelled
    int    getEventVersion();    // 数据结构版本，用于后续演进
    Object getData();            // 业务事实快照
    default String getSubject() { return null; } // 业务实体标识，如 trip/10001
}
```

### 3.2 EventEnvelope 信封字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `eventId` | String | 全局唯一事件 ID（UUID） |
| `eventType` | String | 事件编码 |
| `eventVersion` | int | 事件数据版本 |
| `source` | String | 来源系统，当前固定 `shuttle-management` |
| `subject` | String | 业务实体标识（如 `trip/10001`、`driver/42`） |
| `occurredAt` | LocalDateTime | 业务事实发生时间 |
| `publishedAt` | LocalDateTime | 发布到应用内事件机制的时间 |
| `operator` | String | 操作人，单机构无登录上下文，固定 `system` |
| `triggerMode` | String | 触发方式 `AUTO` / `MANUAL`，当前 `AUTO` |
| `tenantId` | String | 租户标识（单机构 Silo，暂为空） |
| `correlationId` | String | 关联业务操作 ID（暂为空） |
| `data` | Object | 业务数据快照 |

### 3.3 sys_event_box 表

| 列 | 说明 |
|----|------|
| `event_id` | 唯一键（VARCHAR 64） |
| `event_type` | 事件编码 |
| `event_version` | 事件版本 |
| `source` | 来源系统 |
| `subject` | 业务实体标识 |
| `payload` | 完整事件信封 JSON（TEXT） |
| `operator` / `trigger_mode` / `tenant_id` / `correlation_id` | 信封冗余字段 |
| `occurred_at` | 业务发生时间 |
| `created_date` / `last_modified_date` 等 | 继承自 `AbstractAuditable` 的审计字段 |

索引：`idx_sys_event_box_type_created(event_type, created_date)`、`idx_sys_event_box_subject(subject)`。

---

## 4. 已注册事件

| 事件编码 | 实现类 | subject | 快照字段 | 触发点 |
|----------|--------|---------|----------|--------|
| `trip.cancelled` | `TripCancelledEvent` | `trip/{id}` | tripId, gtfsTripId, tripLabel, routeId, routeShortName, directionId | `TripService.cancelTrip`（物理删除班次，删前抓快照） |
| `driver.status_changed` | `DriverStatusChangedEvent` | `driver/{id}` | driverId, name, oldStatus, newStatus | `DriverService.update`（状态变化才发） |

> 说明：`trip.cancelled` 的 `cancelTrip` 是物理删除，当前未接入运行时取消入口（避免误删真实班次），故该事件在正常界面操作下暂不触发。

---

## 5. 如何接入一个新事件

### 5.1 定义事件编码常量

在 `EventTypes` 中新增：

```java
public static final String VEHICLE_STATUS_CHANGED = "vehicle.status_changed";
```

### 5.2 在注册表登记

在 `EventTypeRegistry` 构造器中 `register(EventTypes.VEHICLE_STATUS_CHANGED)`。

### 5.3 实现 DomainEvent

```java
public final class VehicleStatusChangedEvent implements DomainEvent {
    private final VehicleStatusChangedData data;
    public VehicleStatusChangedEvent(VehicleStatusChangedData data) { this.data = data; }

    @Override public String getEventType() { return EventTypes.VEHICLE_STATUS_CHANGED; }
    @Override public int getEventVersion() { return 1; }
    @Override public Object getData() { return data; }
    @Override public String getSubject() { return "vehicle/" + data.vehicleId(); }

    public record VehicleStatusChangedData(Long vehicleId, String plateNumber,
                                           String oldStatus, String newStatus) {}
}
```

### 5.4 在业务方法内发布

```java
@Service
@RequiredArgsConstructor
public class VehicleService {
    private final DomainEventPublisher eventPublisher;

    @Transactional                      // 必须处于事务中
    public Vehicle update(...) {
        Vehicle saved = vehicleRepository.save(...);
        if (statusChanged) {
            eventPublisher.publish(new VehicleStatusChangedEvent(
                new VehicleStatusChangedEvent.VehicleStatusChangedData(...)));
        }
        return saved;
    }
}
```

---

## 6. 事务性约束（红线，务必遵守）

1. 发布事件的业务方法**必须**标注 `@Transactional`。
2. `EventBoxListener` 为同步 `@EventListener`，**禁止**使用 `@Async`。
3. EventBox 与业务数据共用同一数据库、同一事务管理器，**禁止**使用 `REQUIRES_NEW`。
4. **禁止**使用 `@TransactionalEventListener(AFTER_COMMIT)`（无法保证原子性）。
5. 写入异常向上传播，使业务事务一并回滚。
6. 事件编码必须在 `EventTypeRegistry` 注册后才会放行，否则抛 `IllegalArgumentException`。
7. **gtfs 包实体零改动**：只对外层发事件快照，不得为发事件而修改 `domain/entity/gtfs` 的字段/语义。

---

## 7. 启动 CDC

### 7.1 前置条件

- Docker 可用，且能访问镜像源 `docker.m.daocloud.io`。
- MySQL 已启用 binlog（`ROW` 格式）并开启 SSL（连接器配置 `database.ssl.mode=required`）。
- 已创建 Debezium 数据库账号（需 `SELECT` / `RELOAD` / `SHOW DATABASES` / `REPLICATION SLAVE` / `REPLICATION CLIENT` 权限）。

标准建账号 SQL（示例）：

```sql
CREATE USER 'debezium'@'%' IDENTIFIED BY 'debezium';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium'@'%';
FLUSH PRIVILEGES;
```

### 7.2 启动基础设施

```bash
docker compose -f kafka-compose.yml up -d
```

编排包含三个服务：

| 服务 | 镜像 | 容器名 | 端口 |
|------|------|--------|------|
| Kafka（KRaft 单节点） | apache/kafka:3.7.0 | shuttle-kafka | 9092（内部） |
| Debezium Connect | debezium/connect:3.0.0.Final | shuttle-debezium | 8083 |
| Kafbat UI | kafbat/kafka-ui:latest | shuttle-kafbat-ui | 9090 |

### 7.3 挂载连接器

```bash
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d @scripts/debezium-eventbox-plain.json
```

连接器要点（`scripts/debezium-eventbox-plain.json`）：

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `connector.class` | MySqlConnector | |
| `table.include.list` | `ucmp_shuttle.sys_event_box` | 仅捕获 Outbox 表 |
| `snapshot.mode` | `schema_only` | 不回溯历史，只捕获增量 |
| `topic.prefix` | `shuttle` | 生成 topic 前缀 |
| `database.ssl.mode` | `required` | 匹配业务库 SSL 连接 |
| `include.schema.changes` | `false` | 不发 schema 变更事件 |

最终 CDC 事件落在 topic：**`shuttle.ucmp_shuttle.sys_event_box`**。

### 7.4 验证

1. 访问 Kafbat UI：`http://127.0.0.1:9090`，确认集群 `shuttle-kafka` ONLINE。
2. 触发一个事件（如修改司机状态），观察 topic `shuttle.ucmp_shuttle.sys_event_box` 是否出现新消息，`after.payload` 即为完整事件信封 JSON。

---

## 8. 运维要点与已知坑

1. **Debezium 3.x 与 MySQL 8.4**：MySQL 8.4 移除了 `SHOW MASTER STATUS`，必须用 Debezium 3.x（使用 `SHOW BINARY LOG STATUS`）；Debezium 2.7 会报语法错误。
2. **镜像环境变量前缀**：核心 worker 变量（`BOOTSTRAP_SERVERS`、`CONFIG_STORAGE_TOPIC`、`OFFSET_STORAGE_TOPIC`、`STATUS_STORAGE_TOPIC`、`KEY_CONVERTER`、`VALUE_CONVERTER` 等）**无前缀**；任意自定义 worker 配置（如 `schemas.enable=false`）必须加 `CONNECT_` 前缀才会被透传。
3. **Outbox Event Router 未启用**：`sys_event_box` 非 Debezium 标准 outbox 表结构，Event Router 需要额外字段映射（`table.field.event.type` 等）。当前先用纯 CDC 验证（`debezium-eventbox-plain.json`）；带 `outbox` transform 的 `debezium-eventbox-connector.json` 保留待打磨。
4. **JSON 序列化**：CDC 用 `JsonConverter` 且 `schemas.enable=false`，未用 Avro；Schema Registry（Apicurio）暂不挂，待上 Avro 演进时再加。
5. **单机拓扑**：`kafka-compose.yml` 的 replication factor 均为 1，仅本地开发验证；生产需多节点 + 提高副本因子。

---

## 9. 相关文件清单

| 路径 | 说明 |
|------|------|
| `src/main/java/me/ningyu/app/shuttle/event/**` | 事件中心实现 |
| `kafka-compose.yml` | Kafka + Debezium Connect + Kafbat UI 编排 |
| `scripts/debezium-eventbox-plain.json` | 正式 CDC 连接器配置 |
| `scripts/debezium-eventbox-connector.json` | 带 Outbox Router 的待打磨版本 |
