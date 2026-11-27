package me.ningyu.app.shuttle.event.meta;

import me.ningyu.app.shuttle.event.core.EventTypes;
import me.ningyu.app.shuttle.event.domain.CaptainUpdatedEvent;
import me.ningyu.app.shuttle.event.domain.DriverStatusChangedEvent;
import me.ningyu.app.shuttle.event.domain.DriverUpdatedEvent;
import me.ningyu.app.shuttle.event.domain.TripCancelledEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件元数据提供器：事件编码 → 中文名 + 数据快照字段。
 *
 * <p>字段的 {@code name}/{@code type} 通过反射 data record 的组件自动生成——新增事件或给
 * 快照加字段时，无需再手工维护字段列表；中文 {@code label} 与可选 {@code enumValues} 由
 * 集中映射表补充，未映射的字段回退用字段名本身作为 label。
 *
 * <p>复杂类型字段（如 {@code List<FieldChange> changes}）会被自动跳过，不作为占位符或条件字段，
 * 避免模板里拿到无法渲染的对象。
 */
@Component
public class EventMetaProvider
{
    /** 事件编码 → 中文名。 */
    private static final Map<String, String> EVENT_NAMES = new LinkedHashMap<>();

    /** 事件编码 → 数据快照 record 类型（反射字段用）。 */
    private static final Map<String, Class<?>> EVENT_DATA_TYPES = new LinkedHashMap<>();

    static
    {
        EVENT_NAMES.put(EventTypes.TRIP_CANCELLED, "班次停运");
        EVENT_DATA_TYPES.put(EventTypes.TRIP_CANCELLED, TripCancelledEvent.TripCancelledData.class);

        EVENT_NAMES.put(EventTypes.DRIVER_STATUS_CHANGED, "司机状态变更");
        EVENT_DATA_TYPES.put(EventTypes.DRIVER_STATUS_CHANGED, DriverStatusChangedEvent.DriverStatusChangedData.class);

        EVENT_NAMES.put(EventTypes.DRIVER_UPDATED, "司机信息更新");
        EVENT_DATA_TYPES.put(EventTypes.DRIVER_UPDATED, DriverUpdatedEvent.DriverUpdatedData.class);

        EVENT_NAMES.put(EventTypes.CAPTAIN_UPDATED, "车长信息更新");
        EVENT_DATA_TYPES.put(EventTypes.CAPTAIN_UPDATED, CaptainUpdatedEvent.CaptainUpdatedData.class);
    }

    /** 字段名 → 中文 label（跨事件复用；缺失回退字段名）。 */
    private static final Map<String, String> FIELD_LABELS = Map.ofEntries(
            Map.entry("tripId", "班次ID"),
            Map.entry("gtfsTripId", "GTFS班次ID"),
            Map.entry("tripLabel", "班次标识"),
            Map.entry("routeId", "线路ID"),
            Map.entry("routeShortName", "线路短名"),
            Map.entry("directionId", "方向"),
            Map.entry("driverId", "司机ID"),
            Map.entry("captainId", "车长ID"),
            Map.entry("name", "姓名"),
            Map.entry("oldStatus", "原状态"),
            Map.entry("newStatus", "新状态")
    );

    /** 事件编码 → (字段名 → 中文 label)：同名字段按事件区分，优先于通用 FIELD_LABELS。 */
    private static final Map<String, Map<String, String>> EVENT_FIELD_LABELS = Map.of(
            EventTypes.DRIVER_STATUS_CHANGED, Map.of("name", "司机姓名"),
            EventTypes.DRIVER_UPDATED, Map.of("name", "司机姓名"),
            EventTypes.CAPTAIN_UPDATED, Map.of("name", "车长姓名")
    );

    /** 字段名 → 枚举值（供下拉/多选值控件；无枚举则为 null）。 */
    private static final Map<String, List<String>> FIELD_ENUMS = Map.of(
            "oldStatus", List.of("在岗", "休假", "停用"),
            "newStatus", List.of("在岗", "休假", "停用")
    );

    /** 返回全部事件类型元数据（顺序稳定，与 EVENT_DATA_TYPES 注册顺序一致）。 */
    public List<EventTypeMeta> eventTypes()
    {
        List<EventTypeMeta> result = new ArrayList<>();
        for (Map.Entry<String, Class<?>> e : EVENT_DATA_TYPES.entrySet())
        {
            String code = e.getKey();
            result.add(new EventTypeMeta(code, EVENT_NAMES.getOrDefault(code, code), fieldsOf(code, e.getValue())));
        }
        return result;
    }

    /** 反射 data record 的组件字段，过滤复杂类型，映射中文 label 与枚举值。 */
    private List<FieldMeta> fieldsOf(String eventCode, Class<?> dataType)
    {
        List<FieldMeta> fields = new ArrayList<>();
        RecordComponent[] components = dataType.getRecordComponents();
        if (components == null)
        {
            return fields;
        }
        for (RecordComponent rc : components)
        {
            Class<?> type = rc.getType();
            if (!isScalar(type))
            {
                continue; // 跳过复杂类型（List/Map/对象等）
            }
            String name = rc.getName();
            fields.add(new FieldMeta(name, labelOf(eventCode, name), typeOf(type), FIELD_ENUMS.get(name)));
        }
        return fields;
    }

    /** 中文 label 查找：事件特定 label 优先，其次通用 label，最后回退字段名本身。 */
    private String labelOf(String eventCode, String fieldName)
    {
        Map<String, String> eventLabels = EVENT_FIELD_LABELS.get(eventCode);
        if (eventLabels != null)
        {
            String label = eventLabels.get(fieldName);
            if (label != null)
            {
                return label;
            }
        }
        return FIELD_LABELS.getOrDefault(fieldName, fieldName);
    }

    /** 仅简单标量类型可作为占位符/条件字段。 */
    private static boolean isScalar(Class<?> type)
    {
        if (type.isPrimitive() || type.isEnum())
        {
            return true;
        }
        if (Number.class.isAssignableFrom(type))
        {
            return true;
        }
        if (type == String.class || type == Boolean.class || type == Character.class)
        {
            return true;
        }
        return type == LocalDate.class || type == LocalDateTime.class || type == LocalTime.class;
    }

    /** 映射为前端条件编辑器的字段类型。 */
    private static String typeOf(Class<?> type)
    {
        if (type == Long.class || type == long.class)
        {
            return "LONG";
        }
        if (type == Integer.class || type == int.class || type == Short.class || type == short.class
                || type == Byte.class || type == byte.class)
        {
            return "INTEGER";
        }
        if (type == Double.class || type == double.class || type == Float.class || type == float.class
                || type == BigDecimal.class)
        {
            return "DOUBLE";
        }
        if (type == Boolean.class || type == boolean.class)
        {
            return "BOOLEAN";
        }
        if (type == LocalDate.class || type == LocalDateTime.class || type == LocalTime.class)
        {
            return "DATETIME";
        }
        return "STRING";
    }

    public record EventTypeMeta(String code, String name, List<FieldMeta> fields)
    {
    }

    public record FieldMeta(String name, String label, String type, List<String> enumValues)
    {
    }
}
