package com.dansmultipro.opsapps.config;

//import com.dansmultipro.opsapps.filter.RateLimiterFilter;
import com.dansmultipro.opsapps.filter.TokenFilter;
import com.dansmultipro.opsapps.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public List<RequestMatcher> getMatchers() {
        var matchers = new ArrayList<RequestMatcher>();
        matchers.add(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/auth/login"));
        matchers.add(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/auth/refresh"));
        matchers.add(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/api/users/register"));
        matchers.add(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/api/users/verify"));
        return matchers;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder){
        var provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception{
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(rateLimiterFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
