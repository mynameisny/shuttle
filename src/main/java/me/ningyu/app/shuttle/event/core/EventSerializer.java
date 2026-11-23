package me.ningyu.app.shuttle.event.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

/**
 * 事件序列化器：把事件信封序列化为 JSON 存入 EventBox 的 payload 字段。
 */
@Component
public class EventSerializer
{
    private final ObjectMapper mapper;

    public EventSerializer()
    {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public String toJson(Object value)
    {
        try
        {
            return mapper.writeValueAsString(value);
        }
        catch (JsonProcessingException e)
        {
            throw new IllegalStateException("事件序列化失败: " + value, e);
        }
    }
}
