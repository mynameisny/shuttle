# Shuttle - 通用交通运输系统

参考 GTFS（Google Transit Feed Specification）数据模型设计的通用交通运输系统，支持公交线路、企业班车、园区摆渡车、景区接驳车等多种交通场景。

## 技术栈

- Java 21
- Spring Boot 4.0.1
- Spring Data JPA / Hibernate
- MySQL
- Maven

## 快速开始

```bash
mvn spring-boot:run
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- API 文档: http://localhost:8080/api-docs

## 核心设计原则

1. **Line（线路）与 Route（运行方向）分离**：Line 是业务线路，Route 是具体运行方向
2. **StopArea（站点名称）与 Stop（停靠点）分离**：StopArea 是用户看到的站名，Stop 是实际停车位置
3. **Trip（班次）独立建模**：某一天某一时间执行的一次 Route
4. **RouteStop 关联 Stop**：而非 StopArea，确保去程/返程停靠不同位置

## 业务约束（Service 层校验）

- 同一时间，司机不能开两辆车
- 同一时间，车辆不能跑两条线
- "停靠点"（Stop）包含 GPS 坐标，"站点区域"（StopArea）不包含
- "到达/出发时间"属于"线路方向上的停靠行为"，应放在 RouteStop 中

## 参考文档

- [CLAUDE.md](./CLAUDE.md) - 开发指导
- [GUIDES.md](./GUIDES.md) - 系统需求规格
