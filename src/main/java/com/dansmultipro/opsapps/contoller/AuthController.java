package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.auth.LoginRequestDto;
import com.dansmultipro.opsapps.dto.auth.LoginResponseDto;
import com.dansmultipro.opsapps.service.UserService;
import com.dansmultipro.opsapps.util.JwtUtil;
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

    @Value("${jwt.token.expiration}")
    private Long expirationDuration;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        Timestamp duration  = Timestamp.valueOf(LocalDateTime.now().plusSeconds(expirationDuration));
        var user = userService.getUserByEmailAndPassword(requestDto.getEmail(), requestDto.getPassword());

        var auth = new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword());
        authenticationManager.authenticate(auth);

        var token = jwtUtil.generateToken(user.getId().toString(), duration);

        return new ResponseEntity<>(new LoginResponseDto(
                user.getUserName(),
                user.getRole().getCode(),
                token
        ),
                HttpStatus.OK
        );
    }

}
