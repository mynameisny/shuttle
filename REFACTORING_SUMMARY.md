# GTFS 重构总结

本次重构参考 GTFS（Google Transit Feed Specification）数据模型，对班车管理系统进行了全面升级，支持多组织（SaaS）、线路版本管理、实时车辆定位等能力。

## 重构概览

### 实体映射关系

| 旧实体 | 新实体 | 说明 |
|--------|--------|------|
| Route | Line | 线路（对应 GTFS routes.txt） |
| RouteDirection | Route | 线路方向（对应 GTFS trips + shapes） |
| PhysicalStop | Stop | 停靠点（对应 GTFS stops.txt） |
| - | StopArea | 站点区域（对应 GTFS parent_station） |
| - | Organization | 组织/租户（多租户扩展） |
| Shift | Trip | 班次（对应 GTFS trips.txt） |
| - | VehicleLocation | 车辆位置（对应 GTFS-Realtime VehiclePosition） |
| DrivingLicense | Driver（合并） | 驾照信息内嵌到 Driver |
| StopCoordinate | Stop（合并） | 坐标直接内嵌到 Stop |

### 删除的实体

- `RouteDirection` → 被 `Route` 替代
- `PhysicalStop` → 被 `Stop` 替代
- `StopCoordinate` → 合并到 `Stop`
- `DrivingLicense` → 合并到 `Driver`
- `Shift` → 被 `Trip` 替代

### 删除的枚举

- `CoordinateSystem` → StopCoordinate 已删除
- `GPSPriority` → OperationLogEntry 简化
- `ShiftSwapStatus` → Shift 已被 Trip 替代

### 新增的枚举

- `LineStatus` - 线路状态（ACTIVE/INACTIVE）
- `RouteStatus` - 线路方向状态（ACTIVE/INACTIVE）
- `TripStatus` - 班次状态（SCHEDULED/IN_PROGRESS/COMPLETED/CANCELLED）
- `VehicleStatus` - 车辆状态（ACTIVE/MAINTENANCE/INACTIVE）
- `DriverStatus` - 司机状态（ACTIVE/INACTIVE）

### 更新的枚举

- `Direction` - 方向类型更新为 OUTBOUND/INBOUND/LOOP/CUSTOM

## 新增文件

### 实体类
- `Organization.java` - 组织/租户
- `Line.java` - 线路
- `StopArea.java` - 站点区域
- `VehicleLocation.java` - 车辆位置
- `Trip.java` - 班次（替代 Shift）

### 枚举类
- `LineStatus.java`
- `RouteStatus.java`
- `TripStatus.java`
- `VehicleStatus.java`
- `DriverStatus.java`

### Repository
- `OrganizationRepository.java`
- `LineRepository.java`
- `StopAreaRepository.java`
- `StopRepository.java`
- `TripRepository.java`
- `VehicleLocationRepository.java`

### Service
- `LineService.java`
- `TripService.java`

### Controller
- `LineController.java`
- `TripController.java`

### Model/DTO
- `CreateLineRequest.java`
- `UpdateLineRequest.java`

## 重命名/重写文件

### 实体类
- `Route.java` - 重写为线路方向（原 RouteDirection 的角色）
- `RouteStop.java` - 更新关联 Route 和 Stop
- `Stop.java` - 重写为停靠点（含 GPS，原 PhysicalStop 的角色）
- `Vehicle.java` - 新增 agency 关联，简化字段
- `Driver.java` - 新增 agency 关联，合并 DrivingLicense
- `StopPhoto.java` - 更新关联 Stop
- `OperationLogEntry.java` - 更新关联 Trip 和 Stop
- `RideRecord.java` - 更新关联 Trip
- `PassengerPreference.java` - 更新关联 Route 和 Stop

### Repository
- `RouteRepository.java` - 更新为查询 Route（线路方向）
- `RouteStopRepository.java` - 更新关联 Route
- `RideRepository.java` - 更新关联 Trip
- `PassengerPreferenceRepository.java` - 更新关联 Route
- `OperationLogEntryRepository.java` - 更新关联 Trip

### Service
- `RouteService.java` - 重写为线路方向管理
- `PassengerService.java` - 更新引用
- `OperationLogService.java` - 更新引用

### Controller
- `RouteController.java` - 重写为线路方向管理
- `PassengerController.java` - 更新引用
- `OperationLogController.java` - 更新引用

## 删除文件

### 实体类
- `RouteDirection.java`
- `PhysicalStop.java`
- `StopCoordinate.java`
- `DrivingLicense.java`
- `Shift.java`

### 枚举类
- `CoordinateSystem.java`
- `GPSPriority.java`
- `ShiftSwapStatus.java`

### Repository
- `RouteDirectionRepository.java`
- `PhysicalStopRepository.java`
- `ShiftRepository.java`

### Service
- `RouteDirectionService.java`
- `ShuttleScheduleService.java`

### Controller
- `RouteDirectionController.java`
- `ShuttleScheduleController.java`

### Model/DTO
- `CreateRouteDirectionRequest.java`
- `UpdateRouteDirectionRequest.java`
- `ShiftSwapRequest.java`
- `ApproveShiftSwapRequest.java`

## 数据库兼容性

部分实体通过 `@Table(name = "原表名")` 保持数据库表名不变：
- `Route` → 表名 `route`
- `RouteStop` → 表名 `route_direction_stop`
- `Trip` → 表名 `shuttle_schedule`

新增实体将在应用启动时自动创建表：
- `agency`
- `line`
- `stop_area`
- `vehicle_location`

## GTFS 完全兼容扩展（2026-07-07）

### 新增实体（10个）

| 实体 | GTFS 文件 | 说明 |
|------|-----------|------|
| ServiceCalendar | calendar.txt | 服务日历（工作日/节假日） |
| CalendarDate | calendar_dates.txt | 特殊日期（临时加开/停运） |
| Shape | shapes.txt | 线路形状点（精细轨迹） |
| FareAttribute | fare_attributes.txt | 票价规则 |
| FareRule | fare_rules.txt | 票价适用范围 |
| Frequency | frequencies.txt | 发车频率 |
| Transfer | transfers.txt | 换乘规则 |
| Pathway | pathways.txt | 站内通道 |
| Level | levels.txt | 站内楼层 |
| FeedInfo | feed_info.txt | 数据源信息 |

### 新增枚举（11个）

- RouteType - 线路类型（TRAM/SUBWAY/RAIL/BUS/FERRY等）
- LocationType - 位置类型（STOP/STATION/ENTRANCE_EXIT等）
- WheelchairAccessibility - 无障碍乘车信息
- BikeAllowance - 是否允许携带自行车
- PaymentMethod - 支付方式
- TransferPermission - 允许换乘次数
- ExceptionType - 异常类型（加开/停运）
- PathwayMode - 通道类型（步道/楼梯/扶梯/电梯等）
- ExactTimes - 是否精确时间
- Timepoint - 时间精度
- TransferType - 换乘类型

### 现有实体扩展

- Organization - 新增 url、timezone、lang、phone、email 字段
- Line - 新增 routeType、textColor、sortOrder、url 字段
- StopArea - 新增 zoneId、url、wheelchairBoarding、level 字段
- Stop - 新增 zoneId、url、code、locationType、wheelchairBoarding、level 字段
- RouteStop - 新增 arrivalTime、departureTime、timepoint、stopHeadsign、pickupType、dropOffType 等字段
- Trip - 新增 serviceCalendar、tripHeadsign、tripShortName、blockId、wheelchairAccessible、bikesAllowed 字段

### 新增 Repository（10个）

- ServiceCalendarRepository
- CalendarDateRepository
- ShapeRepository
- FareAttributeRepository
- FareRuleRepository
- FrequencyRepository
- TransferRepository
- PathwayRepository
- LevelRepository
- FeedInfoRepository

## 后续任务

1. 实现 StopController（停靠点 CRUD API）
2. 实现 OrganizationController（组织管理 API）
3. 实现 StopAreaController（站点区域管理 API）
4. 实现 VehicleLocationController（车辆位置 API）
5. 实现 ServiceCalendarController（服务日历 API）
6. 实现 FareController（票价管理 API）
7. 实现 TransferController（换乘规则 API）
8. 集成 Kafka 消息队列（RideStartedEvent）
9. 集成移动端 GPS 上报
10. 实现 Reservation（预约乘车）

---

**重构完成日期**: 2026-07-07
**设计参考**: GTFS (General Transit Feed Specification)
