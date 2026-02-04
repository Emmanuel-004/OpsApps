package com.dansmultipro.opsapps.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VerificationSuccessMailPojo {
    private String email;
    private String message;
}
