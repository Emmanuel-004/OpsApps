package com.dansmultipro.opsapps.filter;

import com.dansmultipro.opsapps.dto.ErrorResponseDto;
import com.dansmultipro.opsapps.exception.RateLimitExceededException;
import com.dansmultipro.opsapps.util.RateLimiterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final RateLimiterUtil rateLimiterUtil;
    private final List<RequestMatcher> requestMatchers;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean matched = requestMatchers.stream()
                    .anyMatch(requestMatcher -> requestMatcher.matches(request));

            if (matched) {
                String ip = request.getRemoteAddr();
                validateRateLimit(ip);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(responseWithJson(e.getMessage()));
        }
    }

    private void validateRateLimit(String ip) {

        if (!rateLimiterUtil.tryConsume(ip)) {
            Duration penalty = rateLimiterUtil.getNewDuration(ip);
            rateLimiterUtil.extendRefill(ip, penalty);

            String formatted = String.format("%02d:%02d",
                    penalty.toMinutesPart(),
                    penalty.toSecondsPart());

            throw new RateLimitExceededException(
                    "Rate limit exceeded. Please retry after " + formatted + " minutes");
        }
    }

    private String responseWithJson(String message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new ErrorResponseDto<>(message));
    }
}
