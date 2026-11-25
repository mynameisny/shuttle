package me.ningyu.app.shuttle.notification.recipient;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义用户解析器：从规则接收人配置的 {@code customValue} 解析手机号（逗号分隔）。
 */
@Component
public class CustomUsersResolver implements RecipientResolver
{
    public static final String TYPE = "CUSTOM_USERS";

    @Override
    public String getType()
    {
        return TYPE;
    }

    @Override
    public List<Recipient> resolve(RecipientContext context)
    {
        String customValue = context.customValue();
        if (customValue == null || customValue.isBlank())
        {
            return List.of();
        }
        List<Recipient> result = new ArrayList<>();
        for (String phone : customValue.split("[,，]"))
        {
            String p = phone.trim();
            if (p.isEmpty())
            {
                continue;
            }
            Map<String, String> contacts = new LinkedHashMap<>();
            contacts.put("SMS", p);
            contacts.put("INBOX", p);
            result.add(new Recipient(p, "CUSTOM", p, contacts));
        }
        return result;
    }
}
