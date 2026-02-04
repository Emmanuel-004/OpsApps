package com.dansmultipro.opsapps.unit;

import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.constant.TransactionStatusCode;
import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionCustomerResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionRequestDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionResponseDto;
import com.dansmultipro.opsapps.model.*;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.*;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.impl.TransactionServiceImpl;
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
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionStatusRepository transactionStatusRepository;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private PaymentGateawayAdminRepository paymentGateawayAdminRepository;

    @Mock
    private PaymentGateawayRepository paymentGateawayRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeneratorUtil generatorUtil;

    @Mock
    private PrincipalService principalService;

    @Mock
    private MailUtil mailUtil;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void shouldReturnAllTransactions_whenUserIsSA() {
        transactionService.setPrincipalService(principalService);

        UUID userId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(userId.toString());

        Role saRole = new Role();
        saRole.setCode(RoleCode.SA.name());
        User saUser = new User();
        saUser.setId(userId);
        saUser.setRole(saRole);

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of((page - 1), size, Sort.by("createdAt").descending());

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setCode("TRX-001");
        transaction.setNominal(BigDecimal.valueOf(100000));

        Product product = new Product();
        product.setName("Product 1");
        transaction.setProduct(product);

        User customer = new User();
        customer.setUserName("customer1");
        transaction.setCustomer(customer);

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setName("Bank ABC");
        transaction.setPaymentGateway(paymentGateaway);

        TransactionStatus status = new TransactionStatus();
        status.setName("Processing");
        transaction.setStatus(status);

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(saUser));
        Mockito.when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);

        PageResponseDto<TransactionResponseDto> result = transactionService.getAllTransactions(page, size);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("TRX-001", result.getData().getFirst().getTransactionCode());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(userId);
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).findAll(pageable);
    }

    @Test
    public void shouldReturnAllTransactions_whenUserIsPG() {
        transactionService.setPrincipalService(principalService);

        UUID userId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(userId.toString());

        Role pgRole = new Role();
        pgRole.setCode(RoleCode.PG.name());
        User pgUser = new User();
        pgUser.setId(userId);
        pgUser.setRole(pgRole);

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of((page - 1), size, Sort.by("createdAt").descending());

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setCode("TRX-002");

        Product product = new Product();
        product.setName("Product 1");
        transaction.setProduct(product);

        User customer = new User();
        customer.setUserName("customer1");
        transaction.setCustomer(customer);

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setName("Bank ABC");
        transaction.setPaymentGateway(paymentGateaway);

        TransactionStatus status = new TransactionStatus();
        status.setName("Processing");
        transaction.setStatus(status);


        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(pgUser));
        Mockito.when(transactionRepository.findAllByAdminId(userId, pageable)).thenReturn(transactionPage);

        PageResponseDto<TransactionResponseDto> result = transactionService.getAllTransactions(page, size);

        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(Mockito.any());
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).findAllByAdminId(userId, pageable);
    }

    @Test
    public void shouldReturnTransactionsByCustomer_whenUserIsCustomer() {
        transactionService.setPrincipalService(principalService);

        UUID userId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(userId.toString());

        Role customerRole = new Role();
        customerRole.setCode(RoleCode.CUS.name());
        User customer = new User();
        customer.setId(userId);
        customer.setRole(customerRole);

        int page = 1;
        int size = 5;
        Pageable pageable = PageRequest.of((page - 1), size, Sort.by("createdAt").descending());

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setCode("TRX-003");
        transaction.setNominal(BigDecimal.valueOf(50000));

        Product product = new Product();
        product.setName("Product 2");
        transaction.setProduct(product);

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setName("Bank XYZ");
        transaction.setPaymentGateway(paymentGateaway);

        TransactionStatus status = new TransactionStatus();
        status.setName("Success");
        transaction.setStatus(status);

        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 1);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(customer));
        Mockito.when(transactionRepository.findAllByCustomer_id(userId, pageable)).thenReturn(transactionPage);

        PageResponseDto<TransactionCustomerResponseDto> result = transactionService.getAllTransactionsByCustomer(page, size);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("TRX-003", result.getData().getFirst().getTransactionCode());

        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(userId);
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).findAllByCustomer_id(userId, pageable);
    }

    @Test
    public void shouldCreateTransactionSuccessfully() {
        transactionService.setPrincipalService(principalService);

        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID paymentGatewayId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(customerId.toString());

        Role customerRole = new Role();
        customerRole.setCode(RoleCode.CUS.name());
        User customer = new User();
        customer.setId(customerId);
        customer.setRole(customerRole);
        customer.setEmail("customer@example.com");

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(paymentGatewayId);
        paymentGateaway.setName("Bank ABC");

        Product product = new Product();
        product.setId(productId);
        product.setName("Product Premium");

        TransactionStatus processingStatus = new TransactionStatus();
        processingStatus.setId(UUID.randomUUID());
        processingStatus.setCode(TransactionStatusCode.PROCESSING.name());
        processingStatus.setName("Processing");

        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setProductId(productId.toString());
        requestDto.setPaymentGateawayId(paymentGatewayId.toString());
        requestDto.setAmount(BigDecimal.valueOf(150000));
        requestDto.setVirtualAccount("888801234567890");

        String generatedCode = "TRX-ABC123XYZ456";
        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(UUID.randomUUID());
        savedTransaction.setCode(generatedCode);
        savedTransaction.setCustomer(customer);
        savedTransaction.setPaymentGateway(paymentGateaway);
        savedTransaction.setProduct(product);
        savedTransaction.setStatus(processingStatus);
        savedTransaction.setNominal(requestDto.getAmount());
        savedTransaction.setVirtualAccount(requestDto.getVirtualAccount());

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransaction(savedTransaction);
        transactionHistory.setStatus(processingStatus);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(paymentGateawayRepository.findById(paymentGatewayId)).thenReturn(Optional.of(paymentGateaway));
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.when(transactionStatusRepository.findByCode(TransactionStatusCode.PROCESSING.name()))
                .thenReturn(Optional.of(processingStatus));
        Mockito.when(generatorUtil.generateCode(20)).thenReturn("ABC123XYZ456");
        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(savedTransaction);
        Mockito.when(transactionHistoryRepository.save(Mockito.any())).thenReturn(transactionHistory);

        CreateResponseDto result = transactionService.createTransaction(requestDto);

        Assertions.assertEquals(savedTransaction.getId(), result.getId());
        Assertions.assertEquals("Transaction created successfully", result.getMessage());

        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(customerId);
        Mockito.verify(paymentGateawayRepository, Mockito.atLeast(1)).findById(paymentGatewayId);
        Mockito.verify(productRepository, Mockito.atLeast(1)).findById(productId);
        Mockito.verify(transactionStatusRepository, Mockito.atLeast(1)).findByCode(TransactionStatusCode.PROCESSING.name());
        Mockito.verify(generatorUtil, Mockito.atLeast(1)).generateCode(20);
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(transactionHistoryRepository, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void shouldUpdateTransactionToApprovedSuccessfully() {
        transactionService.setPrincipalService(principalService);

        UUID transactionId = UUID.randomUUID();
        UUID gatewayAdminId = UUID.randomUUID();
        UUID paymentGatewayId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(gatewayAdminId.toString());

        Role pgRole = new Role();
        pgRole.setCode(RoleCode.PG.name());
        User pgAdmin = new User();
        pgAdmin.setId(gatewayAdminId);
        pgAdmin.setRole(pgRole);

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(paymentGatewayId);
        paymentGateaway.setName("Bank ABC");

        TransactionStatus processingStatus = new TransactionStatus();
        processingStatus.setCode(TransactionStatusCode.PROCESSING.name());
        processingStatus.setName("Processing");

        TransactionStatus approvedStatus = new TransactionStatus();
        approvedStatus.setId(UUID.randomUUID());
        approvedStatus.setCode(TransactionStatusCode.APPROVED.name());
        approvedStatus.setName("Approved");

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setCode("TRX-001");
        transaction.setStatus(processingStatus);
        transaction.setPaymentGateway(paymentGateaway);

        User customer = new User();
        customer.setId(UUID.randomUUID());
        customer.setEmail("customer@example.com");
        transaction.setCustomer(customer);

        PaymentGateawayAdmin paymentGatewayAdmin = new PaymentGateawayAdmin();
        paymentGatewayAdmin.setPaymentGateaway(paymentGateaway);
        paymentGatewayAdmin.setGateawayAdmin(pgAdmin);

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(transactionId);
        updatedTransaction.setStatus(approvedStatus);
        updatedTransaction.setCustomer(customer);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransaction(updatedTransaction);
        transactionHistory.setStatus(approvedStatus);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        Mockito.when(userRepository.findById(gatewayAdminId)).thenReturn(Optional.of(pgAdmin));
        Mockito.when(paymentGateawayAdminRepository.findByGateawayAdminAndPaymentGateaway(pgAdmin, paymentGateaway)).thenReturn(Optional.of(paymentGatewayAdmin));
        Mockito.when(transactionStatusRepository.findByCodeEqualsIgnoreCase("APPROVED")).thenReturn(Optional.of(approvedStatus));
        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(updatedTransaction);
        Mockito.when(transactionHistoryRepository.save(Mockito.any())).thenReturn(transactionHistory);

        CommonResponseDto result = transactionService.updateTransaction(transactionId.toString(), "APPROVED");

        Assertions.assertEquals("Transaction Approved", result.getMessage());

        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).findById(transactionId);
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(gatewayAdminId);
        Mockito.verify(paymentGateawayAdminRepository, Mockito.atLeast(1))
                .findByGateawayAdminAndPaymentGateaway(Mockito.any(), Mockito.any());
        Mockito.verify(transactionStatusRepository, Mockito.atLeast(1)).findByCodeEqualsIgnoreCase(TransactionStatusCode.APPROVED.name());
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(transactionHistoryRepository, Mockito.atLeast(1)).save(Mockito.any());
    }

    @Test
    public void shouldUpdateTransactionToRejectedSuccessfully() {
        transactionService.setPrincipalService(principalService);

        UUID transactionId = UUID.randomUUID();
        UUID gatewayAdminId = UUID.randomUUID();
        UUID paymentGatewayId = UUID.randomUUID();
        AuthorizationPojo principal = new AuthorizationPojo(gatewayAdminId.toString());

        Role pgRole = new Role();
        pgRole.setCode(RoleCode.PG.name());
        User pgAdmin = new User();
        pgAdmin.setId(gatewayAdminId);
        pgAdmin.setRole(pgRole);

        PaymentGateaway paymentGateaway = new PaymentGateaway();
        paymentGateaway.setId(paymentGatewayId);
        paymentGateaway.setName("Bank XYZ");

        TransactionStatus processingStatus = new TransactionStatus();
        processingStatus.setCode(TransactionStatusCode.PROCESSING.name());
        processingStatus.setName("Processing");

        TransactionStatus rejectedStatus = new TransactionStatus();
        rejectedStatus.setId(UUID.randomUUID());
        rejectedStatus.setCode(TransactionStatusCode.REJECTED.name());
        rejectedStatus.setName("Rejected");

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setCode("TRX-002");
        transaction.setStatus(processingStatus);
        transaction.setPaymentGateway(paymentGateaway);

        User customer = new User();
        customer.setId(UUID.randomUUID());
        customer.setEmail("customer2@example.com");
        transaction.setCustomer(customer);

        PaymentGateawayAdmin paymentGatewayAdmin = new PaymentGateawayAdmin();
        paymentGatewayAdmin.setPaymentGateaway(paymentGateaway);
        paymentGatewayAdmin.setGateawayAdmin(pgAdmin);

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(transactionId);
        updatedTransaction.setStatus(rejectedStatus);
        updatedTransaction.setCustomer(customer);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransaction(updatedTransaction);
        transactionHistory.setStatus(rejectedStatus);

        Mockito.when(principalService.getPrincipal()).thenReturn(principal);
        Mockito.when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        Mockito.when(userRepository.findById(gatewayAdminId)).thenReturn(Optional.of(pgAdmin));
        Mockito.when(paymentGateawayAdminRepository.findByGateawayAdminAndPaymentGateaway(pgAdmin, paymentGateaway)).thenReturn(Optional.of(paymentGatewayAdmin));
        Mockito.when(transactionStatusRepository.findByCodeEqualsIgnoreCase("REJECTED")).thenReturn(Optional.of(rejectedStatus));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(updatedTransaction);
        Mockito.when(transactionHistoryRepository.save(Mockito.any())).thenReturn(transactionHistory);

        CommonResponseDto result = transactionService.updateTransaction(transactionId.toString(), "REJECTED");

        Assertions.assertEquals("Transaction Rejected", result.getMessage());

        Mockito.verify(principalService, Mockito.atLeast(1)).getPrincipal();
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).findById(transactionId);
        Mockito.verify(userRepository, Mockito.atLeast(1)).findById(gatewayAdminId);
        Mockito.verify(paymentGateawayAdminRepository, Mockito.atLeast(1)).findByGateawayAdminAndPaymentGateaway(Mockito.any(), Mockito.any());
        Mockito.verify(transactionStatusRepository, Mockito.atLeast(1)).findByCodeEqualsIgnoreCase(Mockito.any());
        Mockito.verify(transactionRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verify(transactionHistoryRepository, Mockito.atLeast(1)).save(Mockito.any());
    }
}
