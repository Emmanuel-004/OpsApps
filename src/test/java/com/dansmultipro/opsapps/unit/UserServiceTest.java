package com.dansmultipro.opsapps.unit;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.user.*;
import com.dansmultipro.opsapps.model.PaymentGateaway;
import com.dansmultipro.opsapps.model.PaymentGateawayAdmin;
import com.dansmultipro.opsapps.model.Role;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.*;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.impl.UserServiceImpl;
import com.dansmultipro.opsapps.util.GeneratorUtil;
import com.dansmultipro.opsapps.util.MailUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PaymentGateawayRepository paymentGateawayRepository;

    @Mock
    private PaymentGateawayAdminRepository paymentGateawayAdminRepository;

    @Mock
    private GeneratorUtil generatorUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MailUtil mailUtil;

    @Mock
    private PrincipalService principalService;

    @Mock
    private BaseService baseService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetAllUsers_whenRequestValid() {
        userService.setPrincipalService(principalService);

        UUID saId = UUID.randomUUID();

        Role saRole = new Role();
        saRole.setCode("SA");

        User admin = new User();
        admin.setId(saId);
        admin.setRole(saRole);

        AuthorizationPojo principal = new AuthorizationPojo(saId.toString(), saRole.getCode());

        int page = 1;
        int size = 5;

        Pageable pageable =  PageRequest.of((page - 1), size);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUserName("user1");
        user1.setEmail("user1@test.com");
        user1.setRole(new Role());
        user1.getRole().setName("Customer");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUserName("user2");
        user2.setEmail("user2@test.com");
        user2.setRole(new Role());
        user2.getRole().setName("Customer");

        List<User> users = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(saId)).thenReturn(Optional.of(admin));
        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);

        PageResponseDto<UserResponseDto> result = userService.getAllUsers(page, size);

        Assertions.assertEquals("user1", result.getData().getFirst().getUserName());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(saId);
        Mockito.verify(userRepository, Mockito.atLeast(1)).findAll(pageable);
    }

    @Test
    void testGetUserById_whenIdValid() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setRole(new Role());
        user.getRole().setName("Customer");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(userId.toString());

        Assertions.assertEquals(userId, result.getId());
        Assertions.assertEquals("test@example.com", result.getEmail());
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(Mockito.any());
    }

    @Test
    void testRegisterCustomer_whenRequestValid() {
        userService.setUserRepository(userRepository);
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setUserName("newuser");
        requestDto.setEmail("newuser@example.com");
        requestDto.setPassword("password123");

        Role customerRole = new Role();
        customerRole.setCode("CUS");

        Role systemRole = new Role();
        systemRole.setCode("SYS");

        User systemUser = new User();
        systemUser.setId(UUID.randomUUID());
        systemUser.setRole(systemRole);

        User savedUser = new User();
        UUID userId = UUID.randomUUID();
        savedUser.setId(userId);
        savedUser.setEmail("test@email.com");
        savedUser.setActivationCode("AWQ14G");

        Mockito.when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        Mockito.when(roleRepository.findByCode("CUS")).thenReturn(Optional.of(customerRole));
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        Mockito.when(generatorUtil.generateCode(6)).thenReturn("123456");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(savedUser);
        Mockito.when(userRepository.findByRole_Code(Mockito.any())).thenReturn(Optional.of(systemUser));

        CreateResponseDto result = userService.registerCustomer(requestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getId());

        Mockito.verify(userRepository, Mockito.atLeast(1)).existsByEmail(Mockito.any());
        Mockito.verify(userRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(userRepository, Mockito.atLeast(1)).findByRole_Code(Mockito.any());
        Mockito.verify(roleRepository, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atLeast(1)).encode("password123");
        Mockito.verify(generatorUtil).generateCode(6);
    }

    @Test
    void testVerifiedCustomer_whenRequestValid() {
        String email = "test@example.com";
        String verificationCode = "123456";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setIsActive(false);
        user.setActivationCode(verificationCode);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.saveAndFlush(Mockito.any())).thenReturn(user);

        CommonResponseDto result = userService.verifiedCustomer(email, verificationCode);

        Assertions.assertEquals("verification successfully",  result.getMessage());
        Mockito.verify(userRepository, Mockito.atLeast(1)).findByEmail(email);
        Mockito.verify(userRepository, Mockito.atLeast(1)).saveAndFlush(Mockito.any());
    }

    @Test
    void testChangePassword_whenRequestValid() {
        userService.setPrincipalService(principalService);

        UUID userId = UUID.randomUUID();

        Role userRole = new Role();
        userRole.setCode(RoleCode.CUS.name());

        AuthorizationPojo principal = new AuthorizationPojo(userId.toString(), userRole.getCode());

        ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto();
        requestDto.setOldPassword("oldPassword");
        requestDto.setNewPassword("newPassword");

        User user = new User();
        user.setId(userId);
        user.setPassword("encodedOldPassword");
        user.setVersion(0);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setVersion(1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito. when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        Mockito.when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        Mockito.when(userRepository.saveAndFlush(Mockito.any())).thenReturn(updatedUser);

        UpdateResponseDto result = userService.changePassword(requestDto);

        Assertions.assertEquals(1, result.getVersion());
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(userId);
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).saveAndFlush(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atLeast(1)).encode("newPassword");
        Mockito.verify(passwordEncoder, Mockito.atLeast(1)).matches(Mockito.any(), Mockito.any());
    }

    @Test
    void testUpdateUser_whenRequestValid() {
        userService.setPrincipalService(principalService);

        UUID userId = UUID.randomUUID();

        Role userRole = new Role();
        userRole.setCode(RoleCode.CUS.name());

        AuthorizationPojo principal = new AuthorizationPojo(userId.toString(),  userRole.getCode());
        principal.setId(userId.toString());

        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setUsername("updatedUsername");
        requestDto.setVersion(0);

        User user = new User();
        user.setId(userId);
        user.setVersion(0);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setVersion(1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.saveAndFlush(Mockito.any())).thenReturn(updatedUser);

        UpdateResponseDto result = userService.updateUser(userId.toString(), requestDto);

        Assertions.assertEquals(1, result.getVersion());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).saveAndFlush(Mockito.any());
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(Mockito.any());
    }

    @Test
    void testRegisterPaymentGateway_whenRequestValid() {
        userService.setPrincipalService(principalService);

        UUID superAdminId = UUID.randomUUID();
        UUID paymentGatewayId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Role saRole = new Role();
        saRole.setCode(RoleCode.SA.name());

        User superAdmin = new User();
        superAdmin.setId(superAdminId);
        superAdmin.setRole(saRole);

        AuthorizationPojo principal = new AuthorizationPojo(superAdminId.toString(),  saRole.getCode());
        principal.setId(superAdminId.toString());

        PaymentGateawayAdminRequestDto requestDto = new PaymentGateawayAdminRequestDto();
        requestDto.setUserName("pgAdmin");
        requestDto.setEmail("pgadmin@example.com");
        requestDto.setPassword("password123");
        requestDto.setPaymentGateawayId(paymentGatewayId.toString());

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(paymentGatewayId);

        Role pgRole = new Role();
        pgRole.setCode("PG");

        User savedUser = new User();
        savedUser.setId(userId);

        PaymentGateawayAdmin paymentGateawayAdmin = new PaymentGateawayAdmin();
        paymentGateawayAdmin.setPaymentGateaway(paymentGateaway);
        paymentGateawayAdmin.setGateawayAdmin(savedUser);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(superAdminId)).thenReturn(Optional.of(superAdmin));
        Mockito.when(userRepository.existsByEmail("pgadmin@example.com")).thenReturn(false);
        Mockito.when(paymentGateawayRepository.findById(paymentGatewayId)).thenReturn(Optional.of(paymentGateaway));
        Mockito.when(roleRepository.findByCode("PG")).thenReturn(Optional.of(pgRole));
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(savedUser);
        Mockito.when(paymentGateawayAdminRepository.save(Mockito.any())).thenReturn(paymentGateawayAdmin);

        CreateResponseDto result = userService.registerPaymentGateAway(requestDto);

        Assertions.assertEquals(userId, result.getId());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(userRepository, Mockito.atLeast(1)).existsByEmail(Mockito.any());
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(roleRepository, Mockito.atLeast(1)).findByCode(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.atLeast(1)).encode(Mockito.any());
        Mockito.verify(userRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(paymentGateawayAdminRepository, Mockito.atLeast(1)).save(Mockito.any());
    }
}
