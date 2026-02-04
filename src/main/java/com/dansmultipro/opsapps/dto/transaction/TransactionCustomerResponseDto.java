package com.dansmultipro.opsapps.dto.transaction;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class TransactionCustomerResponseDto {
    UUID transactionId;
    String transactionCode;
    BigDecimal amount;
    String virtualAccount;
    String productName;
    String paymentGateawayName;
    String status;
}
