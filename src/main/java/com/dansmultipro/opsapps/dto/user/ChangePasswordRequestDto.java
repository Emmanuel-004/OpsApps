package com.dansmultipro.opsapps.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must at least 6 character")
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must at least 6 character")
    private String newPassword;
}
