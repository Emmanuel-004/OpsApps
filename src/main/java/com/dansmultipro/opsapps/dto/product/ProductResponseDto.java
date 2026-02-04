package com.dansmultipro.opsapps.dto.product;

import lombok.Value;

import java.util.UUID;

@Value
public class ProductResponseDto {
    UUID id;
    String name;
    String code;
}
