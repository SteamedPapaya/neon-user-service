package com.neon.tonari.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpirationMs;  // 리프레시 토큰 만료 시간

    // 액세스 토큰 생성
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    // 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        log.info("getEmailFromToken: " + claims.getSubject());
        return claims.getSubject();
    }

    // 리프레시 토큰 만료 시간 반환
    public long getRefreshTokenExpiry() {
        return refreshTokenExpirationMs;
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}