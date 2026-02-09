package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.config.RabbitMQConfig;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.*;
import com.dansmultipro.opsapps.dto.email.EmailNotificationDto;
import com.dansmultipro.opsapps.dto.user.*;
import com.dansmultipro.opsapps.exception.DataIntegrationException;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.exception.NotUniqueException;
import com.dansmultipro.opsapps.model.PaymentGateaway;
import com.dansmultipro.opsapps.model.PaymentGateawayAdmin;
import com.dansmultipro.opsapps.model.Role;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.*;
import com.dansmultipro.opsapps.service.UserService;
import com.dansmultipro.opsapps.util.GeneratorUtil;
import com.dansmultipro.opsapps.util.MailUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGateawayRepository  paymentGateawayRepository;
    private final PaymentGateawayAdminRepository paymentGateawayAdminRepository;
    private final GeneratorUtil generatorUtil;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final MailUtil mailUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email"));

        return new org.springframework.security.core.userdetails.User(
                email,
                user.getPassword(),
                new ArrayList<>()
        );
    }

    @Override
    public User getUserByEmailAndPassword(String email, String password) {
        User verifyUser = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email")
        );

        if (!passwordEncoder.matches(password, verifyUser.getPassword())) {
            throw new NotAllowedException("Wrong password");
        }

        if (verifyUser.getIsActive().equals(false)) {
            throw new NotAllowedException("User is not active");
        }

        return verifyUser;
    }

    @Cacheable(value = "users", key = "'page:'+page+'size:'+size")
    @Override
    public PageResponseDto<UserResponseDto> getAllUsers(Integer page, Integer size) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID saId = validateId(principal.getId());

        User admin = userRepository.findById(saId).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        if (!admin.getRole().getCode().equals(RoleCode.SA.name())) {
            throw new NotAllowedException("not allowed to access");
        }

        Pageable pageable = PageRequest.of((page - 1), size);
        Page<User> users = userRepository.findAll(pageable);

        List<UserResponseDto> data = users.stream()
                .map(u -> new UserResponseDto(
                        u.getId(),
                        u.getUserName(),
                        u.getEmail(),
                        u.getRole().getName()
                ))
                .toList();

        return new PageResponseDto<>(
                data,
                users.getNumber(),
                users.getSize(),
                users.getTotalPages(),
                users.getTotalElements()
        );
    }

    @Override
    public UserResponseDto getUserById(String id) {
        UUID userId = validateId(id);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole().getName()
        );
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public CreateResponseDto registerCustomer(RegisterRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new NotUniqueException("Email already exists");
        }

        Role role = roleRepository.findByCode(RoleCode.CUS.name()).orElseThrow(
                () -> new NotFoundException("Role code not found")
        );

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        String verificationCode = generatorUtil.generateCode(6);

        User user = new User();
        user.setUserName(requestDto.getUserName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setIsActive(false);
        user.setActivationCode(verificationCode);
        setInitialCreate(user, RoleCode.SYS.name());

        User savedUser = userRepository.save(user);

        sendVerificationEmail(savedUser);

        return new CreateResponseDto(savedUser.getId(), "User registered successfully, check your email for verification code");
    }

    @Override
    @Transactional(rollbackOn =  Exception.class)
    public CommonResponseDto verifiedCustomer(String email, String verificationCode) {
        User existingUser = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found with email")
        );

        if (existingUser.getIsActive() == true) {
            throw new NotAllowedException("User is active");
        }

        if (!existingUser.getActivationCode().equals(verificationCode)) {
            throw new NotAllowedException("Activation code does not match");
        }

        existingUser.setIsActive(true);

        userRepository.saveAndFlush(existingUser);

        return new CommonResponseDto("verification successfully");
    }

    @Override
    public UpdateResponseDto changePassword(ChangePasswordRequestDto requestDto) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID userId = validateId(principal.getId());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );


        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new NotAllowedException("Old password does not match");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());

        user.setPassword(encodedPassword);
        setUpdate(user);

        User updatedUser = userRepository.saveAndFlush(user);

        return new UpdateResponseDto(updatedUser.getVersion(), "Password changed successfully");
    }

    @Override
    public UpdateResponseDto updateUser(String id, UpdateUserRequestDto requestDto) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID userId = validateId(principal.getId());
        UUID requestId = validateId(id);

        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        if (!existingUser.getId().equals(requestId)) {
            if (!existingUser.getRole().getCode().equals(RoleCode.SA.name())) {
                throw new NotAllowedException("not allowed to access");
            }
        }

        if (!existingUser.getVersion().equals(requestDto.getVersion())) {
            throw new DataIntegrationException("Version mismatch");
        }

        existingUser.setUserName(requestDto.getUsername());
        setUpdate(existingUser);

        User updatedUser =  userRepository.saveAndFlush(existingUser);

        return new UpdateResponseDto(updatedUser.getVersion(), "User updated successfully");
    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    public DeleteResponseDto deleteUser(String id) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID adminId = validateId(principal.getId());
        UUID userId = validateId(id);

        User admin =  userRepository.findById(adminId).orElseThrow(
                () -> new NotFoundException("Admin not found")
        );

        if (!RoleCode.SA.name().equals(admin.getRole().getCode())) {
            throw new NotAllowedException("Only admin can delete data");
        }
        User existingUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        if (transactionRepository.existsByCustomer(existingUser)) {
            throw new NotAllowedException("User is already on transactions");
        }

        userRepository.delete(existingUser);

        return new DeleteResponseDto("User has been deleted successfully");

    }

    @CacheEvict(value = "users", allEntries = true)
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public CreateResponseDto registerPaymentGateAway(PaymentGateawayAdminRequestDto requestDto) {
        AuthorizationPojo principal = principalService.getPrincipal();

        User superAdmin = userRepository.findById(UUID.fromString(principal.getId())).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        if (!superAdmin.getRole().getCode().equals(RoleCode.SA.name())) {
            throw new NotAllowedException("Cannot create payment gate away");
        }

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new NotUniqueException("Email already exists");
        }

        PaymentGateaway paymentGateaway = paymentGateawayRepository.findById(UUID.fromString(requestDto.getPaymentGateawayId())).orElseThrow(
                () -> new NotFoundException("Payment gateaway not found")
        );

        Role gatewayRole = roleRepository.findByCode(RoleCode.PG.name()).orElseThrow(
                () -> new NotFoundException("Role not found")
        );

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User();
        user.setUserName(requestDto.getUserName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(gatewayRole);
        user.setIsActive(true);
        user.setActivationCode(null);
        setCreate(user);

        User savedUser = userRepository.save(user);

        PaymentGateawayAdmin admin = new PaymentGateawayAdmin();
        admin.setPaymentGateaway(paymentGateaway);
        admin.setGateawayAdmin(savedUser);
        setCreate(admin);
        paymentGateawayAdminRepository.save(admin);

        return new CreateResponseDto(savedUser.getId(), "Payment gateaway admin created successfully");
    }

    private void sendVerificationEmail(User user) {
        String verificationUrl =
                "http://localhost:8080/api/users/verify"
                        + "?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
                        + "&verificationCode=" + user.getActivationCode();

        String body = mailUtil.buildActivationEmail(user.getUserName(), verificationUrl);

        EmailNotificationDto emailNotificationDto = new EmailNotificationDto();
        emailNotificationDto.setEmail(user.getEmail());
        emailNotificationDto.setSubject("Verification Code - PayKu");
        emailNotificationDto.setBody(body);
        emailNotificationDto.setHtml(true);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_REGISTER_EX,
                RabbitMQConfig.EMAIL_REGISTER_KEY,
                emailNotificationDto
        );
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_REGISTER_QUEUE)
    public void receiveRegisterEmail(EmailNotificationDto notificationDto) {
        mailUtil.sendEmailNotification(notificationDto.getEmail(), notificationDto.getSubject(), notificationDto.getBody(), notificationDto.isHtml());
    }
}
