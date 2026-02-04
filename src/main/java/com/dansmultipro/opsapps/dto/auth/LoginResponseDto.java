package com.dansmultipro.opsapps.dto.auth;

import lombok.Value;

@Value
public class LoginResponseDto {
    String roleCode;
    String email;
    String token;
}
