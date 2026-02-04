package com.dansmultipro.opsapps.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequestDto {

    @Email
    @NotNull(message = "Email cannot null")
    @NotEmpty(message = "Email is required to fill")
    private String email;

    @NotNull(message = "Password cannot null")
    @NotEmpty(message = "Password is required to fill")
    @Size(min = 6, message = "Password must at least 6 character")
    private String password;
}
