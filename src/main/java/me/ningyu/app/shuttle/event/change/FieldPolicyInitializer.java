package me.ningyu.app.shuttle.event.change;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字段变更策略种子：预置敏感字段的默认策略（幂等）。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FieldPolicyInitializer implements ApplicationRunner
{
    private final FieldPolicyRepository policyRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        init("DRIVER", "phone", "手机号", FieldPolicyType.MASK);
        init("DRIVER", "idCardNumber", "身份证号", FieldPolicyType.EXCLUDE);
        init("DRIVER", "licenseNumber", "驾驶证号", FieldPolicyType.EXCLUDE);
        init("CAPTAIN", "phone", "手机号", FieldPolicyType.MASK);
        init("CAPTAIN", "idCardNumber", "身份证号", FieldPolicyType.EXCLUDE);
    }

    private void init(String entityType, String fieldName, String label, FieldPolicyType policy)
    {
        if (policyRepository.findByEntityTypeAndFieldName(entityType, fieldName).isPresent())
        {
            return;
        }
        policyRepository.save(FieldPolicy.builder()
                .entityType(entityType)
                .fieldName(fieldName)
                .fieldLabel(label)
                .policy(policy)
                .build());
        log.info("已初始化字段策略: {}.{} -> {}", entityType, fieldName, policy);
    }
}
