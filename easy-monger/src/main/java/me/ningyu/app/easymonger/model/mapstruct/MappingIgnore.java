package me.ningyu.app.easymonger.model.mapstruct;


import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 映射时忽略公共字段，在controller接口的方法上使用@MappingIgnore注解
 * 参考：
 * - <a href="https://mapstruct.org/documentation/stable/reference/html/#mapping-composition">...</a>
 * - <a href="https://blog.csdn.net/zyw61483/article/details/112990760">...</a>
 */
@Retention(RetentionPolicy.CLASS)
@Mappings(value = {
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "createdDate", ignore = true),
        @Mapping(target = "modifiedDate", ignore = true),
        @Mapping(target = "createdBy", ignore = true),
        @Mapping(target = "lastModifiedBy", ignore = true)
})
public @interface MappingIgnore
{
}
