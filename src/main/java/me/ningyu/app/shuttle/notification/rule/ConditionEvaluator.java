package me.ningyu.app.shuttle.notification.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 条件表达式求值器（JSON AST）。
 *
 * <p>支持组节点 AND / OR / NOT 与叶子节点 EQ / NE / GT / LT / GTE / LTE /
 * IN / NOT_IN / CONTAINS / NOT_CONTAINS / IS_NULL / IS_NOT_NULL。
 * 值支持字符串、数字、布尔、数组；日期相对值 TODAY / TOMORROW。
 */
@Component
public class ConditionEvaluator
{
    private final ObjectMapper mapper;

    public ConditionEvaluator(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

    /**
     * 求值条件表达式；表达式为空视为无条件命中。
     *
     * @param conditionJson JSON AST 字符串（可为 null）
     * @param data          事件数据快照（扁平 Map）
     */
    public boolean evaluate(String conditionJson, Map<String, Object> data)
    {
        if (conditionJson == null || conditionJson.isBlank())
        {
            return true;
        }
        try
        {
            JsonNode root = mapper.readTree(conditionJson);
            return evalNode(root, data);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("条件表达式解析失败: " + conditionJson, e);
        }
    }

    private boolean evalNode(JsonNode node, Map<String, Object> data)
    {
        String op = node.path("operator").asText(null);
        if (op == null)
        {
            return evalLeaf(node, data);
        }
        JsonNode children = node.get("children");
        switch (op)
        {
            case "AND":
                if (children == null || !children.isArray())
                {
                    return false;
                }
                for (JsonNode c : children)
                {
                    if (!evalNode(c, data))
                    {
                        return false;
                    }
                }
                return true;
            case "OR":
                if (children == null || !children.isArray())
                {
                    return false;
                }
                for (JsonNode c : children)
                {
                    if (evalNode(c, data))
                    {
                        return true;
                    }
                }
                return false;
            case "NOT":
                if (children == null || !children.isArray() || children.isEmpty())
                {
                    return false;
                }
                return !evalNode(children.get(0), data);
            default:
                return evalLeaf(node, data);
        }
    }

    private boolean evalLeaf(JsonNode node, Map<String, Object> data)
    {
        String field = node.path("field").asText();
        String op = node.path("operator").asText();
        Object actual = data.get(field);
        JsonNode valueNode = node.get("value");

        switch (op)
        {
            case "IS_NULL":
                return actual == null;
            case "IS_NOT_NULL":
                return actual != null;
            default:
                break;
        }
        if (actual == null)
        {
            return false;
        }
        switch (op)
        {
            case "EQ":
                return eq(actual, norm(valueNode));
            case "NE":
                return !eq(actual, norm(valueNode));
            case "GT":
                return cmp(actual, norm(valueNode)) > 0;
            case "LT":
                return cmp(actual, norm(valueNode)) < 0;
            case "GTE":
                return cmp(actual, norm(valueNode)) >= 0;
            case "LTE":
                return cmp(actual, norm(valueNode)) <= 0;
            case "IN":
                return in(actual, valueNode, true);
            case "NOT_IN":
                return in(actual, valueNode, false);
            case "CONTAINS":
                return contains(actual, norm(valueNode), true);
            case "NOT_CONTAINS":
                return contains(actual, norm(valueNode), false);
            default:
                throw new IllegalArgumentException("不支持的运算符: " + op);
        }
    }

    /** 归一化 JsonNode 值，处理 TODAY / TOMORROW 相对日期。 */
    private Object norm(JsonNode v)
    {
        if (v == null || v.isNull())
        {
            return null;
        }
        if (v.isTextual())
        {
            String s = v.asText();
            if ("TODAY".equals(s))
            {
                return LocalDate.now().toString();
            }
            if ("TOMORROW".equals(s))
            {
                return LocalDate.now().plusDays(1).toString();
            }
            return s;
        }
        if (v.isIntegralNumber())
        {
            return v.asLong();
        }
        if (v.isFloatingPointNumber())
        {
            return v.asDouble();
        }
        if (v.isBoolean())
        {
            return v.asBoolean();
        }
        return v.asText();
    }

    private boolean eq(Object a, Object b)
    {
        if (a == null || b == null)
        {
            return a == b;
        }
        if (isNumber(a) && isNumber(b))
        {
            return toBigDecimal(a).compareTo(toBigDecimal(b)) == 0;
        }
        return String.valueOf(a).equals(String.valueOf(b));
    }

    private int cmp(Object a, Object b)
    {
        if (isNumber(a) && isNumber(b))
        {
            return toBigDecimal(a).compareTo(toBigDecimal(b));
        }
        return String.valueOf(a).compareTo(String.valueOf(b));
    }

    private boolean in(Object actual, JsonNode values, boolean positive)
    {
        if (values == null || !values.isArray())
        {
            return !positive;
        }
        boolean found = false;
        for (JsonNode v : values)
        {
            if (eq(actual, norm(v)))
            {
                found = true;
                break;
            }
        }
        return positive == found;
    }

    private boolean contains(Object actual, Object value, boolean positive)
    {
        boolean found;
        if (actual instanceof Iterable<?> it)
        {
            found = false;
            for (Object o : it)
            {
                if (eq(o, value))
                {
                    found = true;
                    break;
                }
            }
        }
        else
        {
            found = String.valueOf(actual).contains(String.valueOf(value));
        }
        return positive == found;
    }

    private boolean isNumber(Object o)
    {
        return o instanceof Number;
    }

    private BigDecimal toBigDecimal(Object o)
    {
        return new BigDecimal(String.valueOf(o));
    }
}
