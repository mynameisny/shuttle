package me.ningyu.app.locator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "hardCodeAuditAware")
public class LocatorApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(LocatorApplication.class, args);
    }

}
