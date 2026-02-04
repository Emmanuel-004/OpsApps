package com.dansmultipro.opsapps.dto.transactionstatus;

import lombok.Value;

import java.util.UUID;

@Value
public class TransactionStatusResponseDto {
    UUID id;
    String name;
    String code;
}
