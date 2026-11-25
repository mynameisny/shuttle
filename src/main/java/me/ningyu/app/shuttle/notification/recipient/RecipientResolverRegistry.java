package me.ningyu.app.shuttle.notification.recipient;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 接收人解析器注册表：按类型编码查找解析器。
 */
@Component
public class RecipientResolverRegistry
{
    private final Map<String, RecipientResolver> resolvers;

    public RecipientResolverRegistry(List<RecipientResolver> resolverList)
    {
        this.resolvers = resolverList.stream()
                .collect(Collectors.toMap(RecipientResolver::getType, Function.identity()));
    }

    public RecipientResolver get(String type)
    {
        return resolvers.get(type);
    }
}
