package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.user.ChangePasswordRequestDto;
import com.dansmultipro.opsapps.dto.user.PaymentGateawayAdminRequestDto;
import com.dansmultipro.opsapps.dto.user.RegisterRequestDto;
import com.dansmultipro.opsapps.dto.user.UpdateUserRequestDto;
import com.dansmultipro.opsapps.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
