package me.ningyu.app.nuoche;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "hardCodeAuditAware")
public class NuoCheApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(NuoCheApplication.class, args);
    }

}
