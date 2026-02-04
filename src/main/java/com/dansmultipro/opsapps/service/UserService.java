package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.*;
import com.dansmultipro.opsapps.dto.user.*;
import com.dansmultipro.opsapps.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getUserByEmailAndPassword(String email, String password);
    PageResponseDto<UserResponseDto> getAllUsers(Integer page, Integer size);
    UserResponseDto getUserById(String id);
    CreateResponseDto registerCustomer(RegisterRequestDto requestDto);
    CommonResponseDto verifiedCustomer(String email, String verificationCode);
    UpdateResponseDto changePassword(ChangePasswordRequestDto requestDto);
    UpdateResponseDto updateCustomer(String id, UpdateUserRequestDto requestDto);
    DeleteResponseDto deleteCustomer(String id);
    CreateResponseDto registerPaymentGateAway(PaymentGateawayAdminRequestDto requestDto);

}
