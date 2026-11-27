package me.ningyu.app.shuttle.iam.abac;

import me.ningyu.app.shuttle.iam.domain.IamPolicyRepository;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * ABAC（含 RBAC 语义）授权引擎装配：加载 jCasbin 模型与策略适配器。
 *
 * <p>模型采用「角色作为主体属性」的 RBAC + regexMatch 形式，可平滑扩展为更细粒度的
 * 属性表达式（ABAC），统一由 {@code Enforcer} 提供 allow/deny 决策。
 */
@Configuration
public class AbacConfig
{
    @Bean
    public Enforcer abacEnforcer(IamPolicyRepository policyRepository) throws IOException
    {
        String modelText = new String(
                new ClassPathResource("iam/rbac_model.conf").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        Model model = new Model();
        model.loadModelFromText(modelText);
        return new Enforcer(model, new JpaPolicyAdapter(policyRepository));
    }
}
