package com.dansmultipro.opsapps.dto.role;

import lombok.Value;

import java.util.UUID;

@Value
public class RoleResponseDto {
    UUID id;
    String name;
    String code;
}
