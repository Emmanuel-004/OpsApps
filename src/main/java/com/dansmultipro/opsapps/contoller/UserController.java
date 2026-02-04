package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.*;
import com.dansmultipro.opsapps.dto.user.*;
import com.dansmultipro.opsapps.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponseDto<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "5")Integer size
    ) {
        PageResponseDto<UserResponseDto> result = userService.getAllUsers(page, size);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) {
        UserResponseDto result = userService.getUserById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<CreateResponseDto> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        CreateResponseDto response = userService.registerCustomer(registerRequestDto);
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateResponseDto> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequestDto requestDto) {
        UpdateResponseDto response = userService.updateCustomer(id, requestDto);
        return new   ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<UpdateResponseDto> changePassword(@RequestBody @Valid ChangePasswordRequestDto changePasswordRequestDto) {
        UpdateResponseDto response = userService.changePassword(changePasswordRequestDto);
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDto> deleteUser(@PathVariable String id) {
        DeleteResponseDto response = userService.deleteCustomer(id);
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/gateaway-admin")
    public ResponseEntity<CreateResponseDto> createGateawayAdmin(@RequestBody @Valid PaymentGateawayAdminRequestDto requestDto) {
        CreateResponseDto response = userService.registerPaymentGateAway(requestDto);
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
