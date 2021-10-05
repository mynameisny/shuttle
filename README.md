# shuttle
来来往往，川流不息


## Todo
- [ ] 车辆管理
- [ ] 人员管理（司机、车长）
- [ ] 线路管理
- [ ] 轨迹回放


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
