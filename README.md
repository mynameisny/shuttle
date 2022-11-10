# shuttle
来来往往，川流不息

> v1版本仅支持百度坐标系，未来考虑加入Google或腾讯坐标。


## Todo
- [ ] 车辆管理
- [ ] 人员管理（司机、车长）
- [ ] 线路管理
- [ ] 轨迹回放

## 线路管理
### 线路
1. 添加线路
    ```http request
    POST /routes
    ```
2. 删除线路
   ```http request
    DELETE /routes/{code}
    ```

### 站点

### 地址


## 创建实体时同时指定关联关系
```java
@Entity
public class User {
    //..
    private String name;

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();
    //..
}

@Entity
public class Address {
    //..
    @ManyToOne
    private User user;
    //..
}
```

```http request
POST http://localhost:8080/api/users
{
    "name" : "user1",
    "addresses" : [
        "http://localhost:8080/api/addresses/1",
        "http://localhost:8080/api/addresses/2"
        ]
}
```


## 参考
- java - Post an entity with Spring Data REST which has relations - Stack Overflow https://stackoverflow.com/questions/44497114/post-an-entity-with-spring-data-rest-which-has-relations
- https://cloud.baidu.com/doc/IVC/s/Qjwvxoeq3
- https://lbs.amap.com/demo/jsapi-v2/example/marker/replaying-historical-running-data
- https://lbsyun.baidu.com/index.php?title=jspopularGL/guide/trackAnimation
- vue-element-admin https://panjiachen.github.io/vue-element-admin-site/zh/
