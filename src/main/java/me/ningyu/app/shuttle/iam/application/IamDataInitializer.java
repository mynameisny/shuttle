package me.ningyu.app.shuttle.iam.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.iam.domain.AccountStatus;
import me.ningyu.app.shuttle.iam.domain.IamRole;
import me.ningyu.app.shuttle.iam.domain.IamRoleRepository;
import me.ningyu.app.shuttle.iam.domain.IamUser;
import me.ningyu.app.shuttle.iam.domain.IamUserRepository;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * IAM 种子数据：启动时初始化默认角色、默认管理员账号与 ADMIN 全量授权（幂等）。
 *
 * <p>开发环境默认管理员 {@code admin / admin123}，生产部署请务必修改密码或禁用该账号。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IamDataInitializer implements ApplicationRunner
{
    private final IamRoleRepository roleRepository;
    private final IamUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Enforcer enforcer;

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        ensureRole("ADMIN", "管理员", "系统管理员，拥有全部权限");
        ensureRole("OPERATOR", "运营人员", "班车运营管理");
        ensureRole("VIEWER", "查看者", "只读查看");

        if (!userRepository.existsByUsername("admin"))
        {
            userRepository.save(IamUser.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .displayName("系统管理员")
                    .status(AccountStatus.ACTIVE)
                    .build());
            log.info("已初始化默认管理员账号 admin");
        }

        if (!enforcer.hasRoleForUser("admin", "ADMIN"))
        {
            enforcer.addRoleForUser("admin", "ADMIN");
        }
        if (!enforcer.hasPermissionForUser("ADMIN", ".*", ".*"))
        {
            enforcer.addPermissionForUser("ADMIN", ".*", ".*");
            log.info("已授权 ADMIN 角色全部权限");
        }

        // 角色默认权限（幂等）：
        // - OPERATOR 运营人员：业务接口读写，但排除 IAM（账号/角色/权限）
        // - VIEWER   查看者：业务接口只读（GET），同样排除 IAM
        // 资源用「请求 URI + HTTP 方法」表达，正则负向前瞻 (?!iam/) 表示「非 /api/iam/ 前缀」。
        ensurePermission("OPERATOR", "/api/(?!iam/).*", ".*");
        ensurePermission("VIEWER", "/api/(?!iam/).*", "GET");
    }

    private void ensurePermission(String role, String resource, String action)
    {
        if (!enforcer.hasPermissionForUser(role, resource, action))
        {
            enforcer.addPermissionForUser(role, resource, action);
            log.info("已授权 {} 角色：{} {}", role, resource, action);
        }
    }

    private void ensureRole(String code, String name, String description)
    {
        if (roleRepository.existsByCode(code))
        {
            return;
        }
        roleRepository.save(IamRole.builder()
                .code(code)
                .name(name)
                .description(description)
                .build());
        log.info("已初始化角色: {}", code);
    }
}
