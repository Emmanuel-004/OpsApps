package com.dansmultipro.opsapps.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerificationMailPojo {
    private String email;
    private String verificationCode;
}
