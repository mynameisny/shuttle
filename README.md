# shuttle
来来往往，川流不息

> v1版本仅支持百度坐标系，未来考虑加入Google或腾讯坐标。


## Todo
- [ ] 车辆管理
- [ ] 人员管理（司机、车长）
- [ ] 线路管理
- [ ] 轨迹回放

## 线路管理

### REST API
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


## Q&A
- Address实体中的地区编码从哪里来？
 
  无论是线路还站点，最终都是要关联到地址（Address），地址中有一个属性area_code是可以精确到居委会的编码，本项目中的编码均来自Github上的开源项目：modood/Administrative-divisions-of-China


- 如何获得站点的地址？

  可以用微信发送当前位置，然后用百度地图打开，再用百度坐标拾取系统手工对应


## 参考
- java - Post an entity with Spring Data REST which has relations - Stack Overflow https://stackoverflow.com/questions/44497114/post-an-entity-with-spring-data-rest-which-has-relations
- https://cloud.baidu.com/doc/IVC/s/Qjwvxoeq3
- https://lbs.amap.com/demo/jsapi-v2/example/marker/replaying-historical-running-data
- https://lbsyun.baidu.com/index.php?title=jspopularGL/guide/trackAnimation
- vue-element-admin https://panjiachen.github.io/vue-element-admin-site/zh/
- modood/Administrative-divisions-of-China: 中华人民共和国行政区划：省级（省份）、 地级（城市）、 县级（区县）、 乡级（乡镇街道）、 村级（村委会居委会） ，中国省市区镇村二级三级四级五级联动地址数据。 https://github.com/modood/Administrative-divisions-of-China
- 拾取坐标系统 https://api.map.baidu.com/lbsapi/getpoint/index.html
