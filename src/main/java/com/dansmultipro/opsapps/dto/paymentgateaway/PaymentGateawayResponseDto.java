package com.dansmultipro.opsapps.dto.paymentgateaway;

import lombok.Value;

import java.util.UUID;

@Value
public class PaymentGateawayResponseDto {
    UUID id;
    String name;
    String code;
}
