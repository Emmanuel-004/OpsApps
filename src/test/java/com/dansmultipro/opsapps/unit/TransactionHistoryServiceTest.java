package com.dansmultipro.opsapps.unit;

import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.history.TransactionHistoryResponseDto;
import com.dansmultipro.opsapps.model.*;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.TransactionHistoryRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.impl.TransactionHistoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionHistoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private PrincipalService principalService;

    @InjectMocks
    private TransactionHistoryServiceImpl transactionHistoryService;

    @Test
    public void shouldReturnAllHistories_whenUserIsSA() {

        UUID userId = UUID.randomUUID();

        Role saRole = new Role();
        saRole.setCode(RoleCode.SA.name());

        User saUser = new User();
        saUser.setId(userId);
        saUser.setRole(saRole);

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of((page-1), size, Sort.by(Sort.Direction.DESC, "createdAt"));

        TransactionHistory history1 = new TransactionHistory();
        history1.setId(UUID.randomUUID());
        Transaction transaction1 = new Transaction();
        transaction1.setCode("TRX-001");
        history1.setTransaction(transaction1);
        TransactionStatus status1 = new TransactionStatus();
        status1.setName("Success");
        history1.setStatus(status1);
        history1.setCreatedAt(LocalDateTime.now());

        TransactionHistory history2 = new TransactionHistory();
        history2.setId(UUID.randomUUID());
        Transaction transaction2 = new Transaction();
        transaction2.setCode("TRX-002");
        history2.setTransaction(transaction2);
        TransactionStatus status2 = new TransactionStatus();
        status2.setName("Failed");
        history2.setStatus(status2);
        history2.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<TransactionHistory> history = List.of(history1, history2);
        Page<TransactionHistory> historyPage = new PageImpl<>(history, pageable, 2);

        ArgumentCaptor<Specification<TransactionHistory>> captor = ArgumentCaptor.forClass(Specification.class);

        Mockito.when(transactionHistoryRepository.findAll(captor.capture(), Mockito.eq(pageable))).thenReturn(historyPage);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(saUser));

        PageResponseDto<TransactionHistoryResponseDto> result = transactionHistoryService.getAllHistories(page, size, userId.toString(), saRole.getCode());

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("TRX-001", result.getData().getFirst().getCode());

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(transactionHistoryRepository, Mockito.times(1)).findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class));
    }

    @Test
    public void shouldReturnAllHistories_whenUserIsPG() {

        UUID userId = UUID.randomUUID();

        Role pgRole = new Role();
        pgRole.setCode(RoleCode.PG.name());

        User pgUser = new User();
        pgUser.setId(userId);
        pgUser.setRole(pgRole);

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of((page-1), size, Sort.by(Sort.Direction.DESC, "createdAt"));

        TransactionHistory history1 = new TransactionHistory();
        history1.setId(UUID.randomUUID());
        Transaction transaction1 = new Transaction();
        transaction1.setCode("TRX-001");
        history1.setTransaction(transaction1);
        TransactionStatus status1 = new TransactionStatus();
        status1.setName("Success");
        history1.setStatus(status1);
        history1.setCreatedAt(LocalDateTime.now());

        TransactionHistory history2 = new TransactionHistory();
        history2.setId(UUID.randomUUID());
        Transaction transaction2 = new Transaction();
        transaction2.setCode("TRX-002");
        history2.setTransaction(transaction2);
        TransactionStatus status2 = new TransactionStatus();
        status2.setName("Failed");
        history2.setStatus(status2);
        history2.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<TransactionHistory> history = List.of(history1, history2);
        Page<TransactionHistory> historyPage = new PageImpl<>(history, pageable, 2);

        ArgumentCaptor<Specification<TransactionHistory>> captor = ArgumentCaptor.forClass(Specification.class);

        Mockito.when(transactionHistoryRepository.findAll(captor.capture(), Mockito.eq(pageable))).thenReturn(historyPage);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(pgUser));

        PageResponseDto<TransactionHistoryResponseDto> result = transactionHistoryService.getAllHistories(page, size, userId.toString(), pgRole.getCode());

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("TRX-001", result.getData().getFirst().getCode());
        Mockito.verify(transactionHistoryRepository, Mockito.times(1)).findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class));
    }

    @Test
    public void shouldReturnAllHistories_whenUserIsCustomer() {

        UUID userId = UUID.randomUUID();

        Role customerRole = new Role();
        customerRole.setCode(RoleCode.CUS.name());

        User customer = new User();
        customer.setId(userId);
        customer.setRole(customerRole);

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of((page-1), size, Sort.by(Sort.Direction.DESC, "createdAt"));

        TransactionHistory history1 = new TransactionHistory();
        history1.setId(UUID.randomUUID());
        Transaction transaction1 = new Transaction();
        transaction1.setCode("TRX-001");
        history1.setTransaction(transaction1);
        TransactionStatus status1 = new TransactionStatus();
        status1.setName("Success");
        history1.setStatus(status1);
        history1.setCreatedAt(LocalDateTime.now());

        TransactionHistory history2 = new TransactionHistory();
        history2.setId(UUID.randomUUID());
        Transaction transaction2 = new Transaction();
        transaction2.setCode("TRX-002");
        history2.setTransaction(transaction2);
        TransactionStatus status2 = new TransactionStatus();
        status2.setName("Failed");
        history2.setStatus(status2);
        history2.setCreatedAt(LocalDateTime.now().minusHours(1));

        List<TransactionHistory> history = List.of(history1, history2);
        Page<TransactionHistory> historyPage = new PageImpl<>(history, pageable, 2);

        ArgumentCaptor<Specification<TransactionHistory>> captor = ArgumentCaptor.forClass(Specification.class);

        Mockito.when(transactionHistoryRepository.findAll(captor.capture(), Mockito.eq(pageable))).thenReturn(historyPage);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(customer));

        PageResponseDto<TransactionHistoryResponseDto> result = transactionHistoryService.getAllHistories(page, size, userId.toString(), customerRole.getCode());

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("TRX-001", result.getData().getFirst().getCode());
        Mockito.verify(transactionHistoryRepository, Mockito.times(1)).findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class));
    }
}
