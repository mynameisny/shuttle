package me.ningyu.app.shuttle.event.change;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FieldPolicyRepository extends JpaRepository<FieldPolicy, Long>
{
    List<FieldPolicy> findByEntityType(String entityType);

    Optional<FieldPolicy> findByEntityTypeAndFieldName(String entityType, String fieldName);
}
