package com.dansmultipro.opsapps.dto.paymentgateaway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentGateawayRequestDto {

    @NotNull(message = "Name cannot null")
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Code cannot null")
    @NotBlank(message = "Code is required")
    private String code;
}
