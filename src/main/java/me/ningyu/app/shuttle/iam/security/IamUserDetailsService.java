package me.ningyu.app.shuttle.iam.security;

import me.ningyu.app.shuttle.iam.domain.AccountStatus;
import me.ningyu.app.shuttle.iam.domain.IamUser;
import me.ningyu.app.shuttle.iam.domain.IamUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 从 {@code iam_user} 加载登录主体，供 Spring Security 认证使用。
 */
@Service
public class IamUserDetailsService implements UserDetailsService
{
    private final IamUserRepository repository;

    public IamUserDetailsService(IamUserRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        IamUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(user.getStatus() != AccountStatus.ACTIVE)
                .authorities(Collections.emptyList())
                .build();
    }
}
