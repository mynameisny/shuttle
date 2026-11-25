package me.ningyu.app.shuttle.notification.recipient;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import me.ningyu.app.shuttle.domain.entity.extension.TripAssignment;
import me.ningyu.app.shuttle.domain.repo.TripAssignmentRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 班次车长解析器：按事件数据中的 {@code routeId} 解析线路分配的车长（可空，短驳线可能无车长）。
 */
@Component
@RequiredArgsConstructor
public class TripConductorResolver implements RecipientResolver
{
    public static final String TYPE = "TRIP_CONDUCTOR";

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
        if (assignment.isEmpty() || assignment.get().getCaptain() == null)
        {
            return List.of();
        }
        Captain captain = assignment.get().getCaptain();
        return List.of(build(captain.getId(), captain.getName(), captain.getPhone()));
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
        return new Recipient(recipientId, "CAPTAIN", name, contacts);
    }
}
