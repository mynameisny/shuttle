package me.ningyu.app.shuttle.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;

import java.io.IOException;

@Getter
@JsonSerialize(using = Direction.DirectionTypeSerializer.class)
public enum Direction
{
    UPSTREAM("上行", "只到公司"),
    DOWNSTREAM("下行", "只从公司出发"),
    LOOP("环线", "在公司与出发地往返");

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
    
    // 自定义序列化器
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
