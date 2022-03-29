package me.ningyu.app.locator.config;

import lombok.Data;
import org.casbin.adapter.JDBCAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CasbinAdapterConfig
{
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    @Bean
    public JDBCAdapter jdbcAdapter() throws Exception
    {
        return new JDBCAdapter(driverClassName, url, username, password);
    }
}
