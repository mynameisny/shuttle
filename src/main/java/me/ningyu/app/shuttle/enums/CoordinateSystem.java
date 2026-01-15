package me.ningyu.app.shuttle.enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;

import java.io.IOException;

@Getter
@JsonSerialize(using = CoordinateSystem.TypeSerializer.class)
public enum CoordinateSystem
{
    WGS84("World Geodetic System 1984", "GPS原始坐标"),
    GCJ02("Guojia Cehui Ju 02", "高德、腾讯地图"),
    BD09("Baidu Coordinate System 2009","百度地图"),
    CGCS2000("China Geodetic Coordinate System 2000","国家大地坐标系"),
    UNKNOWN("未知", "未知坐标系");

    private final String name;
    private final String description;

    CoordinateSystem(String name, String description)
    {
        this.name = name;
        this.description = description;
    }
    
    public static CoordinateSystem fromName(String name)
    {
        for (CoordinateSystem type : values())
        {
            if (type.name.equalsIgnoreCase(name))
            {
                return type;
            }
        }
        throw new RuntimeException("映射枚举失败");
    }
    
    // 自定义序列化器
    public static class TypeSerializer extends StdSerializer<CoordinateSystem>
    {
        public TypeSerializer()
        {
            super(CoordinateSystem.class);
        }
        
        @Override
        public void serialize(CoordinateSystem value, JsonGenerator gen, SerializerProvider provider) throws IOException
        {
            gen.writeStartObject();
            gen.writeStringField("code", value.name());
            gen.writeStringField("name", value.getName());
            gen.writeStringField("description", value.getDescription());
            gen.writeEndObject();
        }
    }
}
