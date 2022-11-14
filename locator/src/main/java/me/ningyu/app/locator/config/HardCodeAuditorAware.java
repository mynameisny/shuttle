package me.ningyu.app.locator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JPA可以使用当前系统时间分析createdDate和lastModifiedDate，但是createdBy和lastModifiedBy字段呢？JPA将如何识别存储在其中的内容？
 * 要告诉JPA当前登录的用户，我们需要提供AuditorAware的实现并覆盖getCurrentAuditor（）方法。在getCurrentAuditor（）中，我们需要获取当前登录的用户。
 *
 * <p>参考</p>
 * 使用Spring Boot和Spring Data JPA实现审计 -解道Jdon https://www.jdon.com/springboot/spring-data-jpa-auditing.html
 */
@Configuration
public class HardCodeAuditorAware implements AuditorAware<String>
{
    @Bean("hardCodeAuditAware")
    public AuditorAware<String> auditorAware()
    {
        return new HardCodeAuditorAware();
    }

    @Override
    public Optional<String> getCurrentAuditor()
    {
        return Optional.of("Tony");
    }
}

// ------------------ Use below code for spring security --------------------------
/*class SpringSecurityAuditorAware implements AuditorAware<User> {

 public User getCurrentAuditor() {

  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

  if (authentication == null || !authentication.isAuthenticated()) {
   return null;
  }

  return ((MyUserDetails) authentication.getPrincipal()).getUser();
 }
}*/