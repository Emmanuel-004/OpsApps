package com.dansmultipro.opsapps.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;

@Component
public class JwtUtil {

    @Value("${jwt.secretkey.value}")
    private String secretKeyValue;

    public String generateToken(String id, Timestamp timestamp){
        var claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("exp", timestamp);

        var secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyValue));
        var jwtBuilder = Jwts.builder().signWith(secretKey).setClaims(claims);

        return jwtBuilder.compact();
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
}
