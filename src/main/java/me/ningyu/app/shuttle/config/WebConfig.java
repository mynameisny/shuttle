package me.ningyu.app.shuttle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private final UploadProperties props;

    public WebConfig(UploadProperties props)
    {
        this.props = props;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // /uploads/** 映射到配置的磁盘目录（jar 外），物理目录与 URL 解耦
        String dir = Paths.get(props.getDir()).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + dir + "/");
    }
}
