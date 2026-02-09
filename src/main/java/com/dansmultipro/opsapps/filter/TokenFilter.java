package com.dansmultipro.opsapps.filter;

import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final List<RequestMatcher> requestMatchers;
    private final JwtUtil jwtUtil;

    public TokenFilter(List<RequestMatcher> requestMatchers, JwtUtil jwtUtil) {
        this.requestMatchers = requestMatchers;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        var matched = requestMatchers.stream()
                .anyMatch(requestMatcher -> requestMatcher.matches(request));

        if (!matched){
            try {
                var authHeader = request.getHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")){
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                var token = authHeader.substring(7);
                var claims = jwtUtil.validateToken(token);

                var roleCode = claims.get("roleCode").toString();
                var data = new AuthorizationPojo(claims.get("id").toString(), roleCode);

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(roleCode)
                );

                var auth = new UsernamePasswordAuthenticationToken(data, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);

            } catch (Exception e){
                e.printStackTrace();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
