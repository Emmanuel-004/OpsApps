package com.dansmultipro.opsapps.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {

    @NotEmpty(message = "Username is required")
    private String username;
    private Integer version;
}
