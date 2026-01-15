package me.ningyu.app.shuttle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private final ApiVersionInterceptor apiVersionInterceptor;


    public WebConfig(ApiVersionInterceptor apiVersionInterceptor)
    {
        this.apiVersionInterceptor = apiVersionInterceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // 仅拦截 API 路径
        registry.addInterceptor(apiVersionInterceptor).addPathPatterns("/api/**");
    }
}
