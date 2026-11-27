package me.ningyu.app.shuttle.iam.application;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.abac.AccessDecisionService;
import me.ningyu.app.shuttle.iam.domain.IamUser;
import me.ningyu.app.shuttle.iam.domain.IamUserRepository;
import me.ningyu.app.shuttle.iam.security.JwtService;
import me.ningyu.app.shuttle.model.iam.LoginRequest;
import me.ningyu.app.shuttle.model.iam.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务：校验用户名密码 → 签发 JWT → 返回主体与角色。
 */
@Service
@RequiredArgsConstructor
public class AuthService
{
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final IamUserRepository userRepository;
    private final AccessDecisionService accessDecisionService;

    public LoginResponse login(LoginRequest request)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        IamUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + request.username()));

        String token = jwtService.issue(user.getUsername(), user.getId());
        List<String> roles = accessDecisionService.rolesOf(user.getUsername());
        return new LoginResponse(token, "Bearer", user.getUsername(), user.getDisplayName(), roles);
    }
}
