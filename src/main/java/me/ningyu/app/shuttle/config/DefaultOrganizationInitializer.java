package me.ningyu.app.shuttle.config;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.Organization;
import me.ningyu.app.shuttle.domain.repo.OrganizationRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultOrganizationInitializer implements CommandLineRunner
{
    private final OrganizationRepository organizationRepository;
    
    
    public void run(String @NonNull [] args)
    {
        if (organizationRepository.findByCode("DEFAULT").isEmpty())
        {
            Organization org = Organization.builder()
                    .name("默认组织")
                    .code("DEFAULT")
                    .build();
            organizationRepository.save(org);
        }
    }
}
