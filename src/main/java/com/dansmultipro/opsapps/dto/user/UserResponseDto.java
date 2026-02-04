package com.dansmultipro.opsapps.dto.user;

import lombok.Value;

import java.util.UUID;

@Value
public class UserResponseDto {
    UUID id;
    String userName;
    String email;
    String roleName;
}
