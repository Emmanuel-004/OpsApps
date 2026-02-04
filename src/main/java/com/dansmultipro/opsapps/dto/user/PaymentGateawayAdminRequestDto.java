package com.dansmultipro.opsapps.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentGateawayAdminRequestDto {

    @NotNull(message = "Payment gateaway cannot null")
    @NotBlank(message = "payment gateaway is required")
    private String paymentGateawayId;

    @NotBlank(message = "Name is required")
    @NotNull(message = "Username cannot be null")
    private String userName;

    @Email
    @NotBlank(message = "Email is required")
    @NotNull(message = "Username cannot be null")
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Username cannot be null")
    @Size(min = 6, message = "Password must at least 6 character")
    private String password;
}
