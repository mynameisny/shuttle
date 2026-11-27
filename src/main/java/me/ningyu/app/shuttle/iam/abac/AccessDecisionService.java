package me.ningyu.app.shuttle.iam.abac;

import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 授权决策服务（可复用核心）：对「主体 + 资源 + 动作」给出 allow/deny。
 *
 * <p>业务侧与 Spring Security 均通过本服务做授权判断，屏蔽 jCasbin 细节。
 * 角色是主体属性的一种来源：{@code subject} 经 {@code g} 策略映射到角色，
 * 再由 {@code p} 策略按角色匹配资源与动作，实现 ABAC 对 RBAC 的统一覆盖。
 */
@Service
@RequiredArgsConstructor
public class AccessDecisionService
{
    private final Enforcer enforcer;

    /** 判定主体是否可对资源执行动作。 */
    public boolean allow(String subject, String resource, String action)
    {
        return enforcer.enforce(subject, resource, action);
    }

    /** 查询主体拥有的角色列表。 */
    public List<String> rolesOf(String subject)
    {
        return enforcer.getRolesForUser(subject);
    }

    /** 查询主体（含角色继承）可用的权限策略行。 */
    public List<List<String>> permissionsOf(String subject)
    {
        return enforcer.getImplicitPermissionsForUser(subject);
    }
}
