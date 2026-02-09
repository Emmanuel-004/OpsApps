package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.auth.LoginRequestDto;
import com.dansmultipro.opsapps.dto.auth.LoginResponseDto;
import com.dansmultipro.opsapps.dto.auth.RefreshTokenRequestDto;
import com.dansmultipro.opsapps.dto.auth.RefreshTokenResponseDto;
import com.dansmultipro.opsapps.service.UserService;
import com.dansmultipro.opsapps.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        var user = userService.getUserByEmailAndPassword(requestDto.getEmail(), requestDto.getPassword());

        var auth = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        authenticationManager.authenticate(auth);

        var accessToken = jwtUtil.generateAccessToken(user.getId().toString(), user.getRole().getCode());
        var refreshToken = jwtUtil.generateRefreshToken(user.getId().toString(), user.getRole().getCode());
        return new ResponseEntity<>(new LoginResponseDto(
                user.getUserName(),
                user.getRole().getCode(),
                accessToken,
                refreshToken
        ),
                HttpStatus.OK
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refresh(@RequestBody @Valid RefreshTokenRequestDto requestDto) {
        Claims claims = jwtUtil.validateToken(requestDto.getRefreshToken());

        if (!jwtUtil.isRefreshToken(claims)) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (jwtUtil.isTokenExpired(claims)) {
            throw new RuntimeException("Token expired");
        }

        String id = jwtUtil.extractId(claims);
        String roleCode = jwtUtil.extractRoleCode(claims);

        var user = userService.getUserById(id);

        var newAccessToken = jwtUtil.generateAccessToken(id, roleCode);
        var newRefreshToken = jwtUtil.generateRefreshToken(id, roleCode);

        return new ResponseEntity<>(
                new RefreshTokenResponseDto(
                        newAccessToken,
                        newRefreshToken
                ),
                HttpStatus.OK
        );
    }

}
