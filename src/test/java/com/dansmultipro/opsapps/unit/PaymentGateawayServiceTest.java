package com.dansmultipro.opsapps.unit;

import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayRequestDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.UpdatePaymentGateawayRequestDto;
import com.dansmultipro.opsapps.model.PaymentGateaway;
import com.dansmultipro.opsapps.model.Role;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.PaymentGateawayRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.impl.PaymentGateawayServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PaymentGateawayServiceTest {

    @Mock
    private PaymentGateawayRepository paymentGateawayRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PrincipalService principalService;

    @InjectMocks
    private PaymentGateawayServiceImpl paymentGateawayService;

    @Test
    public void shouldReturnData_whenIdValid() {
        UUID id = UUID.randomUUID();

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(id);
        paymentGateaway.setName("paymentGateaway");
        paymentGateaway.setCode("G001");

        Mockito.when(paymentGateawayRepository.findById(id)).thenReturn(Optional.of(paymentGateaway));

        PaymentGateawayResponseDto result = paymentGateawayService.findById(id.toString());

        Assertions.assertEquals(paymentGateaway.getId(), result.getId());
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).findById(Mockito.any());
    }

    @Test
    public void shouldReturnData_whenRequestInvalid() {
        PaymentGateaway paymentGateaway1 = new PaymentGateaway();
        paymentGateaway1.setId(UUID.randomUUID());
        paymentGateaway1.setName("paymentGateaway1");
        paymentGateaway1.setCode("G001");

        PaymentGateaway paymentGateaway2 = new PaymentGateaway();
        paymentGateaway2.setId(UUID.randomUUID());
        paymentGateaway2.setName("paymentGateaway2");
        paymentGateaway2.setCode("G002");

        List<PaymentGateaway> paymentGateawayList = List.of(paymentGateaway1, paymentGateaway2);

        Mockito.when(paymentGateawayRepository.findAll()).thenReturn(paymentGateawayList);

        List<PaymentGateawayResponseDto> result = paymentGateawayService.findAllPaymentGateaway();

        Assertions.assertEquals(paymentGateawayList.size(), result.size());
        Assertions.assertEquals("G001", result.getFirst().getCode());
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).findAll();
    }

    @Test
    public void shouldCreated_whenDataValid() {
        paymentGateawayService.setPrincipalService(principalService);

        UUID adminId = UUID.randomUUID();

        Role role = new Role();
        role.setCode("SA");
        role.setName("Super Administrator");

        User admin = new User();
        admin.setId(adminId);
        admin.setRole(role);

        AuthorizationPojo principal = new AuthorizationPojo(admin.getId().toString(), role.getCode());

        PaymentGateawayRequestDto  requestDto = new PaymentGateawayRequestDto();
        requestDto.setName("paymentGateaway 1");
        requestDto.setCode("G001");

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(UUID.randomUUID());
        paymentGateaway.setName(requestDto.getName());
        paymentGateaway.setCode(requestDto.getCode());

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(paymentGateawayRepository.existsByName(requestDto.getName())).thenReturn(false);
        Mockito.when(paymentGateawayRepository.existsByCode(requestDto.getCode())).thenReturn(false);
        Mockito.when(paymentGateawayRepository.save(Mockito.any())).thenReturn(paymentGateaway);

        CreateResponseDto result = paymentGateawayService.create(requestDto);

        Assertions.assertEquals(result.getId(), paymentGateaway.getId());

        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).existsByName(Mockito.any());
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).existsByCode(Mockito.any());
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void shouldUpdated_whenDataValid() {
        paymentGateawayService.setPrincipalService(principalService);

        UUID adminId = UUID.randomUUID();
        UUID pgId = UUID.randomUUID();

        Role role = new Role();
        role.setCode("SA");
        role.setName("Super Administrator");

        User admin = new User();
        admin.setId(adminId);
        admin.setRole(role);

        AuthorizationPojo principal = new AuthorizationPojo(admin.getId().toString(), role.getCode());

        UpdatePaymentGateawayRequestDto requestDto = new UpdatePaymentGateawayRequestDto();
        requestDto.setName("paymentGateaway1 updated");
        requestDto.setCode("G001");
        requestDto.setVersion(0);

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(pgId);
        paymentGateaway.setName("paymentGateaway1");
        paymentGateaway.setCode("G001");
        paymentGateaway.setVersion(0);

        PaymentGateaway updatedPaymentGateaway = new PaymentGateaway();
        updatedPaymentGateaway.setId(pgId);
        updatedPaymentGateaway.setName(requestDto.getName());
        updatedPaymentGateaway.setCode(requestDto.getCode());
        updatedPaymentGateaway.setVersion(1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(paymentGateawayRepository.findById(pgId)).thenReturn(Optional.of(paymentGateaway));
        Mockito.when(paymentGateawayRepository.save(Mockito.any())).thenReturn(updatedPaymentGateaway);

        UpdateResponseDto result = paymentGateawayService.update(pgId.toString(), requestDto);

        Assertions.assertEquals(1, result.getVersion());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).save(Mockito.any());

    }
}
