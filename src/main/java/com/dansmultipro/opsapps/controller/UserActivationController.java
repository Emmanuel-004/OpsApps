package com.dansmultipro.opsapps.controller;

import com.dansmultipro.opsapps.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserActivationController {

    private final UserService userService;

    @GetMapping("/verify")
    public String verifyUser(
            @RequestParam String email,
            @RequestParam String verificationCode
    ) {
        try {
            userService.verifiedCustomer(email, verificationCode);
            return "email/activation-success";
        } catch (Exception e) {
            return "email/activation-failed";
        }
    }
}
