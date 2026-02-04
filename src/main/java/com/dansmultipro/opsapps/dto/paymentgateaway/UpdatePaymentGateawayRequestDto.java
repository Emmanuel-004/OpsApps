package com.dansmultipro.opsapps.dto.paymentgateaway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePaymentGateawayRequestDto {
    @NotNull(message = "Name cannot null")
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Code cannot null")
    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "version cannot null")
    @NotEmpty(message = "version is required")
    private Integer version;
}
