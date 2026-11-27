package me.ningyu.app.shuttle.iam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：BCrypt 密码、JWT 无状态过滤链，认证 + ABAC 授权。
 *
 * <p>授权不在此用 RBAC 表达式，而是交给 jCasbin：{@code /api/**} 在认证之后
 * 叠加 {@link AbacAuthorizationManager}，以「主体 + 资源(URI) + 动作(HTTP 方法)」
 * 调用 {@code AccessDecisionService.allow} 统一判定。
 *
 * <p>认证边界（用户拍板「乘客端公开、管理端登录」）：乘客端的线路地图 / 班车时刻 /
 * 运营分析等只读查询接口匿名放行；其余 {@code /api/**} 一律要求登录 + 授权。
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig
{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter,
                                                   AbacAuthorizationManager abacAuthorizationManager) throws Exception
    {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 登录接口与登录页、静态资源、API 文档放行
                        .requestMatchers("/api/auth/login", "/login.html",
                                "/css/**", "/js/**", "/vendor/**", "/uploads/**",
                                "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 乘客端公开只读接口（线路地图 / 班车时刻 / 运营分析）
                        .requestMatchers(HttpMethod.GET,
                                "/api/app-config",
                                "/api/routes",
                                "/api/shapes/overview", "/api/shapes/*",
                                "/api/stops", "/api/stops/usage-summary",
                                "/api/trips/active-on/*", "/api/trips/by-route/*",
                                "/api/assignments/route/*",
                                "/api/stop-events/analysis").permitAll()
                        // 其余 API：先认证，再由 jCasbin 按角色授权
                        .requestMatchers("/api/**").access(AuthorizationManagers.allOf(
                                AuthenticatedAuthorizationManager.authenticated(),
                                abacAuthorizationManager))
                        // 静态页面暂放行（前端逐步接入认证）
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder)
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
