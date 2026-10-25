package me.ningyu.app.shuttle.config;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.Agency;
import me.ningyu.app.shuttle.domain.repo.AgencyRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAgencyInitializer implements CommandLineRunner
{
    private final AgencyRepository agencyRepository;

    public void run(String @NonNull [] args)
    {
        if (agencyRepository.findByAgencyId("DEFAULT").isEmpty())
        {
            Agency agency = Agency.builder()
                    .agencyId("DEFAULT")
                    .agencyName("默认运营机构")
                    .agencyUrl("https://example.com")
                    .agencyTimezone("Asia/Shanghai")
                    .agencyLang("zh")
                    .build();
            agencyRepository.save(agency);
        }
    }
}