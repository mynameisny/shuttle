package me.ningyu.app.shuttle.notification.template;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板渲染器：支持 {@code {{变量名}}} 占位符，从事件数据快照取值替换。
 */
@Component
public class TemplateRenderer
{
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    public String render(String template, Map<String, Object> data)
    {
        if (template == null)
        {
            return null;
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find())
        {
            String key = matcher.group(1);
            Object value = data.get(key);
            String replacement = value == null ? "" : String.valueOf(value);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
