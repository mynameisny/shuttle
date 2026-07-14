# 通用交通运输系统需求规格

## 一、系统整体架构

- **后端**：Spring Boot + Spring Data JPA + MySQL，提供 RESTful API
- **前端（Web）**：TypeScript + Ant Design Pro + UmiJS，集成开源地图组件
- **移动端（班车端）**：React Native（Android），负责 GPS 上报与事件上报
- **移动端（乘客端）**：React Native（Android/iOS），支持扫码上车、站点提醒、订阅通知
- **消息中间件**：Kafka（用于发送乘客上车事件、考勤对接等异步通信）
- **审计日志**：所有关键操作需记录操作人、时间、变更内容

---

## 二、数据模型设计（参考 GTFS）

### 核心实体关系

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

### 与 GTFS 对照

#### 实体级对照

| 本设计 | GTFS 对应 | 说明 |
|--------|-----------|------|
| Organization | agency.txt | 交通运营机构（多租户扩展） |
| Line | routes.txt | 业务线路 |
| Route | trips + shapes + direction 的抽象 | 线路方向/变体 |
| StopArea | stops.txt (parent_station) | 站点名称聚合 |
| Stop | stops.txt | 实际停靠点 |
| RouteStop | stop_times.txt | 路线上的停靠顺序 |
| Trip | trips.txt | 一次具体运行 |
| Vehicle | GTFS-Realtime Vehicle | 车辆 |
| VehicleLocation | GTFS-Realtime VehiclePosition | 实时位置 |
| Driver | 无 | 企业运营扩展 |
| Passenger | 无 | 企业运营扩展 |
| RideRecord | 无 | 企业运营扩展 |
| OperationLogEntry | 无 | 企业运营扩展 |

#### 字段级对照

| GTFS 文件 | GTFS 字段 | 本设计实体 | 本设计字段 |
|-----------|-----------|-----------|-----------|
| agency.txt | agency_id | Organization | id |
| agency.txt | agency_name | Organization | name |
| agency.txt | agency_url | Organization | （待扩展） |
| agency.txt | agency_timezone | Organization | （待扩展） |
| agency.txt | agency_phone | Organization | （待扩展） |
| routes.txt | route_id | Line | id |
| routes.txt | agency_id | Line | organization |
| routes.txt | route_short_name | Line | code |
| routes.txt | route_long_name | Line | name |
| routes.txt | route_color | Line | color |
| routes.txt | route_desc | Line | description |
| stops.txt | stop_id | Stop | id |
| stops.txt | stop_name | Stop | name |
| stops.txt | stop_lat | Stop | latitude |
| stops.txt | stop_lon | Stop | longitude |
| stops.txt | parent_station | Stop | stopArea |
| stops.txt | platform_code | Stop | platform |
| stop_times.txt | trip_id | RouteStop | route |
| stop_times.txt | stop_id | RouteStop | stop |
| stop_times.txt | stop_sequence | RouteStop | sequence |
| trips.txt | trip_id | Trip | id |
| trips.txt | route_id | Trip | route |
| trips.txt | service_id | Trip | serviceDate |
| trips.txt | direction_id | Route | direction |
| trips.txt | shape_id | Route | shape |
| GTFS-RT | vehicle.id | Vehicle | id |
| GTFS-RT | vehicle.label | Vehicle | plateNo |
| GTFS-RT | position.latitude | VehicleLocation | latitude |
| GTFS-RT | position.longitude | VehicleLocation | longitude |
| GTFS-RT | position.speed | VehicleLocation | speed |
| GTFS-RT | position.bearing | VehicleLocation | heading |
| GTFS-RT | timestamp | VehicleLocation | gpsTime |

#### 已实现的 GTFS 扩展实体

| GTFS 文件 | 本设计实体 | 说明 |
|-----------|-----------|------|
| calendar.txt | ServiceCalendar | 服务日历（工作日/节假日） |
| calendar_dates.txt | CalendarDate | 特殊日期（临时加开/停运） |
| shapes.txt | Shape | 线路形状点（精细轨迹） |
| fare_attributes.txt | FareAttribute | 票价规则 |
| fare_rules.txt | FareRule | 票价适用范围 |
| frequencies.txt | Frequency | 发车频率 |
| transfers.txt | Transfer | 换乘规则 |
| pathways.txt | Pathway | 站内通道 |
| levels.txt | Level | 站内楼层 |
| feed_info.txt | FeedInfo | 数据源信息 |

#### 现有实体的 GTFS 字段扩展

**Organization（扩展 agency.txt 字段）：**
- url - 组织URL
- timezone - 时区
- lang - 语言
- phone - 联系电话
- email - 联系邮箱

**Line（扩展 routes.txt 字段）：**
- routeType - 线路类型（TRAM/SUBWAY/RAIL/BUS/FERRY等）
- textColor - 文字颜色
- sortOrder - 排序编号
- url - 线路URL

**StopArea（扩展 stops.txt parent_station 字段）：**
- zoneId - 区域编码（票价分区）
- url - 站点URL
- wheelchairBoarding - 无障碍乘车信息
- level - 所属楼层

**Stop（扩展 stops.txt 字段）：**
- zoneId - 区域编码（票价分区）
- url - 站点URL
- code - 站点编码
- locationType - 位置类型（STOP/STATION/ENTRANCE_EXIT等）
- wheelchairBoarding - 无障碍乘车信息
- level - 所属楼层

**RouteStop（扩展 stop_times.txt 字段）：**
- arrivalTime - 计划到达时间
- departureTime - 计划出发时间
- timepoint - 时间精度（APPROXIMATE/EXACT）
- stopHeadsign - 站点头牌文字
- pickupType - 上车类型
- dropOffType - 下车类型
- continuousPickup - 连续上车
- continuousDropOff - 连续下车

**Trip（扩展 trips.txt 字段）：**
- serviceCalendar - 服务日历
- tripHeadsign - 班车头牌文字
- tripShortName - 班次简称
- blockId - 车辆组ID（同一辆车连续执行多个班次）
- wheelchairAccessible - 无障碍设施
- bikesAllowed - 是否允许携带自行车

---

## 三、核心业务子域划分

### 1. 组织管理（Organization）

**实体**：
- `Organization`（组织）
    - 名称、编码
    - 支持多租户 SaaS 模式

**场景**：某公交公司、某企业、某学校、某园区

---

### 2. 车辆管理（Vehicle Management）

**实体**：
- `Vehicle`（车辆）
    - 所属组织、车牌号、类型（公交车/班车/大巴/商务车）、座位数、状态

**用例**：
- 增删改查车辆信息
- 查询空闲车辆（用于排班）

---

### 3. 司机管理（Driver Management）

**实体**：
- `Driver`（司机）
    - 所属组织、姓名、联系电话、驾照号、状态
    - 驾照信息内嵌：性别、准驾车型、出生日期、初次领证时间、生效/到期时间

**约束**：
- 驾照需在有效期内才能参与排班
- 只能与"空闲司机"调班

---

### 4. 线路与站点管理（Line, Route & Stop Management）

**实体**：
- `Line`（线路）
    - 所属组织、编码、名称、颜色、描述、状态
- `Route`（线路方向）
    - 所属线路、名称、方向（OUTBOUND/INBOUND/LOOP/CUSTOM）、版本号、形状、状态
- `StopArea`（站点区域）
    - 名称、编码、城市、备注
    - 用户理解的站名（如"地铁朱辛庄站"）
- `Stop`（停靠点）
    - 所属站点区域、名称、经纬度、站台编号、方位角、备注
    - 车辆真正停车的位置
- `RouteStop`（路线站点）
    - 所属线路方向、停靠点、顺序、距起点距离

**关键设计**：
- Line 不关心具体走向，去程/返程由 Route 定义
- StopArea 不包含经纬度，一个站名可对应多个停靠位置
- RouteStop 永远关联 Stop，而不是 StopArea

---

### 5. 班次管理（Trip Management）

**实体**：
- `Trip`（班次）
    - 线路方向、车辆、司机、运营日期、发车时间、预计到达时间、状态

**核心概念**：Trip = 某一天 + 某司机 + 某车辆 + 某线路方向

**用例**：
- 手动创建单个班次
- 基于模板批量生成排班计划
- 在日历视图中可视化编辑排班

**约束**：
- 同一时间，司机不能开两辆车
- 同一时间，车辆不能跑两条线

---

### 6. 乘客管理（Passenger Management）

**实体**：
- `Passenger`（乘客）
    - 姓名、员工编号、手机号、类型（正式/派遣/借调）
    - 常坐线路及站点偏好
- `PassengerPreference`（乘客偏好）
    - 所属乘客、线路方向、停靠点、是否主要偏好

---

### 7. 乘车记录（Ride Record）

**实体**：
- `RideRecord`（乘车记录）
    - 乘客、班次（Trip）、上车时间、下车时间

**集成点**：
- 乘客扫码上车 → 向 Kafka 发送 `RideStartedEvent`
- 用于 HR 考勤系统："上车即打卡"

---

### 8. 运营记录（Operation Log）

**实体**：
- `OperationLogEntry`（运营记录）
    - 班次（Trip）、停靠点（Stop）、实际到达/出发时间、GPS坐标、数据来源

**数据来源**：
- 班车端 GPS 上报
- 乘客扫码
- 司机手动上报

---

### 9. 车辆定位（Vehicle Location）

**实体**：
- `VehicleLocation`（车辆位置）
    - 车辆、班次（Trip）、经纬度、速度、行驶方向、GPS采集时间

**用途**：
- 地图展示
- ETA（到站预测）
- 偏航检测
- 历史轨迹

---

## 四、系统集成点

| 集成目标 | 触发条件 | 数据格式/协议 |
|--------|--------|-------------|
| HR 考勤系统 | 乘客扫码上车 | Kafka Topic: `ride_started` |
| HR 考勤系统 | 司机上报晚点 | REST API 或 Kafka |
| 地图展示（Web） | 实时车辆位置 | WebSocket / HTTP Polling |
| 站点提醒（乘客 App） | 车辆接近关注站 | FCM / APNs 推送 |

---

## 五、后续可扩展实体

- `ServiceCalendar` - 服务日历（工作日/节假日/周末）
- `Fare` - 票价
- `Reservation` - 班车预约
- `Seat` - 固定座位
- `TripPassenger` - 乘车记录（扩展）
- `Dispatch` - 调度记录
- `Alert` - 运营公告

---

## 六、未来扩展考虑

- **满载率计算**：基于乘客默认偏好 + 实际乘坐记录
- **动态二维码**：班车端生成带时效/班次信息的动态码
- **线路版本管理**：Route.version 支持线路改线
- **预约乘车**：支持班车预约场景
