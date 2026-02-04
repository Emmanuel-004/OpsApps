package com.dansmultipro.opsapps.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class CreateResponseDto {
    UUID id;
    String message;
}
