package me.ningyu.app.shuttle.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringDocConfig
{
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("班车服务")
                        .description("""
                            ## 使用说明
                            - **认证**: 所有请求必须包含 Header `X-Auth-Token`
                            - **版本控制**: 所有请求必须包含 Header `X-API-Version: v1`
                            """)
                        .contact(new Contact().name("Tony").email("mynameisny@126.com").url("https://ningyu.me"))
                        .version("v1")
                )
                .servers(List.of(
                        new Server().description("本地环境").url("http://localhost:8080"),
                        new Server().description("Dev环境").url("https://ucmp-cce-zk.dcloud.cnpc")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("产品原型")
                        .url("https://devtool.ctl.dcloud.cnpc/static/current/ucmp_zk_cce/index.html#id=5f0uqt&p=%E6%97%A5%E5%BF%97%E5%88%86%E6%9E%902_0")
                )
                .components(new Components()
                        .addParameters("apiVersion", new Parameter()
                                .name("X-API-Version")
                                .in("header")
                                .example("v1")
                                .required(true)
                                .description("API版本")
                                .schema(new Schema<>().type("String"))
                        )
                        .addSecuritySchemes("apiToken", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Auth-Token")
                                .description("认证Token")
                        )
                );
    }
}
