# Route 实体 GTFS 规范更新

## 修改概述

按照 GTFS (Google Transit Feed Specification) 标准完全重构了 Route 实体，使其完全兼容 routes.txt 规范。

## 新增字段

### GTFS 规范字段

| 字段名 | GTFS 对应 | 类型 | 说明 |
|--------|-----------|------|------|
| `shortName` | route_short_name | VARCHAR(50) | 线路简名（如"463"） |
| `description` | route_desc | TEXT | 线路描述 |
| `routeType` | route_type | ENUM | 线路类型（公交、地铁、班车等） |
| `routeUrl` | route_url | VARCHAR(255) | 线路官方网址 |
| `routeColor` | route_color | CHAR(6) | 线路颜色（16进制，如FFFFFF） |
| `routeTextColor` | route_text_color | CHAR(6) | 文本颜色（16进制，如000000） |
| `sortOrder` | route_sort_order | INT | 线路排序（用于 UI 显示） |
| `continuousPickup` | continuous_pickup | INT | 连续上车规则 |
| `continuousDropOff` | continuous_drop_off | INT | 连续下车规则 |
| `networkId` | network_id | VARCHAR(50) | 网络ID（扩展字段，用于组织多条线路） |

### continuousPickup/continuousDropOff 取值说明

| 值 | 说明 |
|----|------|
| 0 | 无信息 |
| 1 | 可在整个线路的任意位置上/下车 |
| 2 | 仅在指定的停靠点上/下车 |
| 3 | 乘客必须提前询问驾驶员才能上/下车 |

### routeType 枚举值

| 值 | 类型 | 说明 |
|---|------|------|
| TRAM | 0 | 有轨电车 |
| SUBWAY | 1 | 地铁 |
| RAIL | 2 | 铁路 |
| BUS | 3 | 公交客车 |
| FERRY | 4 | 轮渡 |
| CABLE_TRAM | 5 | 缆车 |
| AERIAL_LIFT | 6 | 索道 |
| FUNICULAR | 7 | 缆索铁路 |
| TROLLEYBUS | 11 | 无轨电车 |
| MONORAIL | 12 | 单轨 |
| SHUTTLE | 100 | 班车（企业班车、园区摆渡车等） |
| COMMUTER | 200 | 通勤车 |

## 修改的字段

### 移除/弃用
- 无直接移除，但以下字段的含义有所调整：
  - `name` - 现在表示方向名称（如"去程"）而非线路名称
  - `direction` - 保留用于去程/返程分类

### 保留字段
- `line` - 关联的 Line（对应 agency）
- `direction` - 方向类型枚举
- `version` - 线路版本管理
- `shape` - 线路形状
- `status` - 线路状态

## 新增关系

### Trip 关联
```java
@OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Trip> trips = new ArrayList<>();
```

添加了 Route → Trip 的一对多关系，确保从 Route 可以直接访问所有班次。

## 数据库迁移建议

### 1. 添加新字段（使用 Flyway 或 Liquibase）

```sql
ALTER TABLE route ADD COLUMN short_name VARCHAR(50) COMMENT '线路简名' AFTER name;
ALTER TABLE route ADD COLUMN description TEXT COMMENT '线路描述' AFTER short_name;
ALTER TABLE route ADD COLUMN route_type VARCHAR(20) COMMENT '线路类型' AFTER direction;
ALTER TABLE route ADD COLUMN route_url VARCHAR(255) COMMENT '线路URL' AFTER route_type;
ALTER TABLE route ADD COLUMN route_color CHAR(6) COMMENT '线路颜色' AFTER route_url;
ALTER TABLE route ADD COLUMN route_text_color CHAR(6) COMMENT '文本颜色' AFTER route_color;
ALTER TABLE route ADD COLUMN sort_order INT COMMENT '线路排序' AFTER version;
ALTER TABLE route ADD COLUMN shape TEXT COMMENT '线路形状' AFTER sort_order;
ALTER TABLE route ADD COLUMN continuous_pickup INT COMMENT '连续上车规则' AFTER shape;
ALTER TABLE route ADD COLUMN continuous_drop_off INT COMMENT '连续下车规则' AFTER continuous_pickup;
ALTER TABLE route ADD COLUMN network_id VARCHAR(50) COMMENT '网络ID' AFTER continuous_drop_off;
```

### 2. 数据初始化建议

- `routeType` - 根据 Line 关联的 Agency 类型推断，或手工分类
- `routeColor`/`routeTextColor` - 可从现有配置迁移或使用默认值
- `continuousPickup`/`continuousDropOff` - 默认为 2（仅指定停靠点）

## API 影响分析

### RouteController 需要调整的方法

- `createRoute()` - 新增字段需要在 DTO 中声明
- `updateRoute()` - 支持新字段的更新
- `getRoute()` - 响应中包含新字段
- `listRoutes()` - 可按 routeType/networkId 等新字段筛选

### 推荐 DTO 字段

```java
@Data
public class RouteDTO {
    private Long id;
    private Long lineId;
    private String name;              // 方向名称
    private String shortName;         // 线路简名
    private String description;
    private Direction direction;
    private RouteType routeType;
    private String routeUrl;
    private String routeColor;
    private String routeTextColor;
    private Integer sortOrder;
    private String shape;
    private Integer continuousPickup;
    private Integer continuousDropOff;
    private String networkId;
    private RouteStatus status;
    private Integer version;
}
```

## 测试建议

1. **单元测试** - 验证 Route 实体的约束条件
2. **集成测试** - 验证 Route ↔ Line ↔ Trip 的关系
3. **API 测试** - 验证新字段的 CRUD 操作
4. **导出测试** - 验证导出 GTFS 格式时新字段的正确性

## 向后兼容性

- 现有代码中对 `route.name` 的依赖需要小心
- 建议在过渡期间同时支持旧字段名和新字段名
- 考虑在 DTO 层面做兼容性处理
