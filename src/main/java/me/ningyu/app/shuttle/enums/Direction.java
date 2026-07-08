package me.ningyu.app.shuttle.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;

import java.io.IOException;

/**
 * 线路方向类型
 * 参考 GTFS 方向定义，扩展支持自定义方向
 */
@Getter
@JsonSerialize(using = Direction.DirectionTypeSerializer.class)
public enum Direction
{
    OUTBOUND("去程", "从起点到终点"),
    INBOUND("返程", "从终点返回起点"),
    LOOP("环线", "循环运行"),
    CUSTOM("自定义", "自定义运行方向");

    private final String name;
    private final String description;

    Direction(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public static Direction fromName(String name)
    {
        for (Direction type : values())
        {
            if (type.name.equalsIgnoreCase(name))
            {
                return type;
            }
        }
        throw new RuntimeException("映射枚举失败");
    }

    public static class DirectionTypeSerializer extends StdSerializer<Direction>
    {
        public DirectionTypeSerializer()
        {
            super(Direction.class);
        }

        @Override
        public void serialize(Direction value, JsonGenerator gen, SerializerProvider provider) throws IOException
        {
            gen.writeStartObject();
            gen.writeStringField("code", value.name());
            gen.writeStringField("name", value.getName());
            gen.writeStringField("description", value.getDescription());
            gen.writeEndObject();
        }
    }
}
