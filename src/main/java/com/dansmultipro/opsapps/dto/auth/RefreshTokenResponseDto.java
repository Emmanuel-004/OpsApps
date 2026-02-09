package com.dansmultipro.opsapps.dto.auth;

import lombok.Value;

@Value
public class RefreshTokenResponseDto {
    String accessToken;
    String refreshToken;
}
