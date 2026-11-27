package me.ningyu.app.shuttle.iam.security;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.abac.AccessDecisionService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * ABAC 授权管理器：把 jCasbin 决策接入 Spring Security 的授权链。
 *
 * <p>每个已认证请求以「主体 = 用户名、资源 = 请求 URI（如 /api/drivers）、
 * 动作 = HTTP 方法（GET/POST/PUT/DELETE）」三元组调用
 * {@link AccessDecisionService#allow}，由 jCasbin 的 g（用户→角色）+
 * p（角色→资源正则→动作正则）策略统一判定 allow/deny。
 *
 * <p>未认证请求直接拒绝（认证由 SecurityFilterChain 里叠加的
 * {@code AuthenticatedAuthorizationManager} 负责，本管理器只做授权）。
 */
@Component
@RequiredArgsConstructor
public class AbacAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext>
{
    private final AccessDecisionService accessDecisionService;

    @Override
    public AuthorizationDecision authorize(Supplier<? extends Authentication> authentication,
                                           RequestAuthorizationContext context)
    {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated())
        {
            return new AuthorizationDecision(false);
        }

        String subject = auth.getName();
        String resource = context.getRequest().getRequestURI();
        String action = context.getRequest().getMethod();
        boolean allowed = accessDecisionService.allow(subject, resource, action);
        return new AuthorizationDecision(allowed);
    }
}
