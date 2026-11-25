package me.ningyu.app.shuttle.notification.recipient;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.entity.extension.TripAssignment;
import me.ningyu.app.shuttle.domain.repo.TripAssignmentRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 班次司机解析器：按事件数据中的 {@code routeId} 解析线路长期分配的司机。
 *
 * <p>注：代班（TripSubstitution）覆盖需事件携带服务日期，本期先解析长期分配司机。
 */
@Component
@RequiredArgsConstructor
public class TripDriverResolver implements RecipientResolver
{
    public static final String TYPE = "TRIP_DRIVER";

    private final TripAssignmentRepository assignmentRepository;

    @Override
    public String getType()
    {
        return TYPE;
    }

    @Override
    public List<Recipient> resolve(RecipientContext context)
    {
        Object routeId = context.data().get("routeId");
        if (routeId == null)
        {
            return List.of();
        }
        Optional<TripAssignment> assignment = assignmentRepository.findByRouteRouteId(String.valueOf(routeId));
        if (assignment.isEmpty() || assignment.get().getDriver() == null)
        {
            return List.of();
        }
        Driver driver = assignment.get().getDriver();
        return List.of(build(driver.getId(), driver.getName(), driver.getPhone()));
    }

    private Recipient build(Long id, String name, String phone)
    {
        String recipientId = (phone != null && !phone.isBlank()) ? phone : String.valueOf(id);
        Map<String, String> contacts = new LinkedHashMap<>();
        if (phone != null && !phone.isBlank())
        {
            contacts.put("SMS", phone);
            contacts.put("INBOX", phone);
        }
        return new Recipient(recipientId, "DRIVER", name, contacts);
    }
}
