package me.ningyu.app.shuttle.iam.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与解析（无状态会话凭证）。
 */
@Service
public class JwtService
{
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret:shuttle-dev-only-secret-key-change-me-32bytes-min}") String secret,
                      @Value("${app.jwt.expiration-ms:86400000}") long expirationMs)
    {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** 签发 token，subject 为用户名，附带 userId。 */
    public String issue(String username, Long userId)
    {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("uid", userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /** 解析 token 得到用户名；token 非法/过期时抛出异常。 */
    public String parseUsername(String token)
    {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
