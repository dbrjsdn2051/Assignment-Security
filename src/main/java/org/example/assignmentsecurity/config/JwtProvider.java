package org.example.assignmentsecurity.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.common.error.SecurityFilterChainException;
import org.example.assignmentsecurity.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHENTICATION_HEADER_PREFIX = "Authorization";

    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] decode = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(decode);
    }

    public String generateToken(Authentication authentication) {
        return TOKEN_PREFIX + Jwts.builder()
                .setSubject(authentication.getName())
                .claim("Role", authentication.getAuthorities())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("유효하지 않는 JWT 서명입니다.");
            throw new SecurityFilterChainException(ErrorCode.INVALID_JWT_SIGNATURE, e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new SecurityFilterChainException(ErrorCode.UNSUPPORTED_JWT_TOKEN, e);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰입니다.");
            throw new SecurityFilterChainException(ErrorCode.INVALID_JWT_TOKEN, e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT token 입니다.");
            throw new SecurityFilterChainException(ErrorCode.EXPIRED_JWT_TOKEN, e);
        }
    }

    public AuthUser getAuthUserForToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

        String nickname = claims.getBody().getSubject();
        List<?> roles = claims.getBody().get("Role", List.class);
        List<Role> authorities = roles.stream()
                .map(e -> Role.valueOf(e.toString()))
                .toList();

        return new AuthUser(nickname, authorities);
    }

}
