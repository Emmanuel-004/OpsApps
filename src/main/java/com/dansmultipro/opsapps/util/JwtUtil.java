package com.dansmultipro.opsapps.util;

import com.dansmultipro.opsapps.constant.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {

    @Value("${jwt.secretkey.value}")
    private String secretKeyValue;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpirationDuration;

    @Value("${jwt.refresh-token.expration}")
    private Long refreshTokenExpirationDuration;

    public String generateAccessToken(String id, String roleCode) {
        return generateToken(id, roleCode, TokenType.ACCESS.name(),  accessTokenExpirationDuration);
    }

    public String generateRefreshToken(String id, String roleCode) {
        return generateToken(id, roleCode, TokenType.REFRESH.name(), refreshTokenExpirationDuration);
    }

    public String generateToken(String id, String roleCode, String tokenType, Long expiration) {
        var claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("roleCode", roleCode);
        claims.put("tokenType", tokenType);

        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + expiration);

        var secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyValue));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(expirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token){
        var secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyValue));
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token has Expired");
        }catch (JwtException jx) {
            throw new RuntimeException("Token invalid");
        }
    }

    public boolean isRefreshToken(Claims claims) {
        return TokenType.REFRESH.name().equals(claims.get("tokenType"));
    }

    public boolean isAccessToken(Claims claims) {
        return TokenType.ACCESS.name().equals(claims.get("tokenType"));
    }

    public String extractId(Claims claims) {
        return claims.get("id").toString();
    }

    public String extractRoleCode(Claims claims) {
        return claims.get("roleCode").toString();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
