package com.dansmultipro.opsapps.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRequestDto {

    @Min(value = 10000, message = "Minimal transaction amount is Rp.10.000")
    private BigDecimal amount;

    @NotBlank(message = "Virtual account is required")
    @NotNull(message = "virtual account cannot null")
    @Min(value = 5, message = "virtual account must be at least 5 character")
    private String virtualAccount;

    @NotNull(message = "Product cannot null")
    @NotBlank(message = "Product is required")
    private String productId;

    @NotNull(message = "Payment Gateaway cannot null")
    @NotBlank(message = "Payment Gateaway is required")
    private String paymentGateawayId;

}
