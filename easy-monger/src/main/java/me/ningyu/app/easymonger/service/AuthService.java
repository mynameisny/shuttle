package me.ningyu.app.easymonger.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.easymonger.domain.auth.*;
import me.ningyu.app.easymonger.model.enums.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService
{
    private final UserRepository userRepository;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        if (StringUtils.isBlank(username))
        {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        
        User user = userRepository.findOne(QUser.user.code.eq(username)).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        
        if (user.getStatus() == UserStatus.LOCKED)
        {
            throw new LockedException("用户已被锁定");
        }
        
        Collection<? extends GrantedAuthority> grantedAuthorities = getAuthorities(user.getRoles());
        return new org.springframework.security.core.userdetails.User(user.getCode(), user.getPassword(), grantedAuthorities);
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles)
    {
        List<String> permissions = getPermissions(roles);
        
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    private List<String> getPermissions(Collection<Role> roles)
    {
        Set<Permission> permissions = new HashSet<>();
        for (Role role : roles)
        {
            permissions.addAll(role.getPermissions());
        }
        return permissions.stream().map(Permission::getCode).collect(Collectors.toList());
    }
}
