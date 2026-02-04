package com.dansmultipro.opsapps.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
