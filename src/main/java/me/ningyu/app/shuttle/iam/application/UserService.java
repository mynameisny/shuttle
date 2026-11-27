package me.ningyu.app.shuttle.iam.application;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.domain.AccountStatus;
import me.ningyu.app.shuttle.iam.domain.IamUser;
import me.ningyu.app.shuttle.iam.domain.IamUserRepository;
import me.ningyu.app.shuttle.model.iam.AssignRolesRequest;
import me.ningyu.app.shuttle.model.iam.CreateUserRequest;
import me.ningyu.app.shuttle.model.iam.ResetPasswordRequest;
import me.ningyu.app.shuttle.model.iam.UpdateUserRequest;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账号用户管理服务（可复用）。角色映射走 jCasbin 的 {@code g} 策略，由 Enforcer 经适配器自动持久化。
 */
@Service
@RequiredArgsConstructor
public class UserService
{
    private final IamUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Enforcer enforcer;

    public List<IamUser> list()
    {
        return userRepository.findAll();
    }

    @Transactional
    public IamUser create(CreateUserRequest request)
    {
        if (userRepository.existsByUsername(request.username()))
        {
            throw new IllegalArgumentException("用户名已存在: " + request.username());
        }
        return userRepository.save(IamUser.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .phone(request.phone())
                .email(request.email())
                .status(AccountStatus.ACTIVE)
                .build());
    }

    @Transactional
    public IamUser update(Long id, UpdateUserRequest request)
    {
        IamUser user = get(id);
        user.setDisplayName(request.displayName());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        if (request.status() != null)
        {
            user.setStatus(request.status());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long id, ResetPasswordRequest request)
    {
        IamUser user = get(id);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id)
    {
        IamUser user = get(id);
        enforcer.deleteRolesForUser(user.getUsername());
        userRepository.delete(user);
    }

    public List<String> rolesOf(Long id)
    {
        return enforcer.getRolesForUser(get(id).getUsername());
    }

    @Transactional
    public void assignRoles(Long id, AssignRolesRequest request)
    {
        IamUser user = get(id);
        enforcer.deleteRolesForUser(user.getUsername());
        List<String> roles = request.roles() == null ? List.of() : request.roles();
        for (String role : roles)
        {
            enforcer.addRoleForUser(user.getUsername(), role);
        }
    }

    private IamUser get(Long id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: id=" + id));
    }
}
