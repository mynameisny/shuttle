# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.1/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.1/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.1/reference/using/devtools.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/4.0.1/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Validation](https://docs.spring.io/spring-boot/4.0.1/reference/io/validation.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.1/reference/web/servlet.html)

### Guides

- 司机不绑定车辆或线路（解耦）
- “司机-线路”通过调度而不是直接绑定的原因：
  - 缺少时间（哪一天，哪个方向）
  - 同一个 RouteDirection 每天都运行 
  - 司机可能只开工作日，或某几天请假 
  - 需要记录 具体日期 + 方向线路 的司机分配
- 排班的最小单位不是 RouteDirection


### 业务约束（需在 Service 层校验）
- 同一时间，司机不能开两辆车
- 同一时间，车辆不能跑两条线
- 班次时间不能早于线路方向中首站发车时间
- “物理站点”（Stop）是地理实体，不应包含任何与“线路”或“时间”相关的业务属性。
  “到达/出发时间”属于“线路方向上的停靠行为”，应放在 RouteDirectionStop 中