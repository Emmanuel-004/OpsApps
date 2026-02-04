package com.dansmultipro.opsapps.dto;

import lombok.Value;

@Value
public class ErrorResponseDto<T> {
    T message;
}
