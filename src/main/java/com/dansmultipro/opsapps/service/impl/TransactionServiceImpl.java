package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.config.RabbitMQConfig;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.constant.TransactionStatusCode;
import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.email.EmailNotificationDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionCustomerResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionRequestDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionResponseDto;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.model.*;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.*;
import com.dansmultipro.opsapps.service.TransactionService;
import com.dansmultipro.opsapps.util.GeneratorUtil;
import com.dansmultipro.opsapps.util.MailUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends BaseService implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionStatusRepository transactionStatusRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final PaymentGateawayAdminRepository paymentGateawayAdminRepository;
    private final PaymentGateawayRepository paymentGateawayRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final GeneratorUtil generatorUtil;
    private final RabbitTemplate  rabbitTemplate;
    private final MailUtil mailUtil;

    @Override
    public PageResponseDto<TransactionResponseDto> getAllTransactions(Integer page, Integer size) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID userId = validateId(principal.getId());

        User requestingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validatePageAndSize(page, size);

        Pageable pageable =  PageRequest.of((page - 1), size, Sort.by("createdAt").descending());
        Page<Transaction> transactions;
        List<TransactionResponseDto> listDto = new ArrayList<>();

        if (requestingUser.getRole().getCode().equals(RoleCode.PG.name())) {

            transactions = transactionRepository.findAllByAdminId(userId, pageable);

        }  else if (requestingUser.getRole().getCode().equals(RoleCode.SA.name())) {
            transactions = transactionRepository.findAll(pageable);

        } else {
            throw new NotAllowedException("User role not allowed to view transactions");
        }

        for (Transaction transaction : transactions.getContent()) {
            listDto.add(
                    new TransactionResponseDto(
                            transaction.getId(),
                            transaction.getCode(),
                            transaction.getNominal(),
                            transaction.getVirtualAccount(),
                            transaction.getProduct().getName(),
                            transaction.getCustomer().getUserName(),
                            transaction.getPaymentGateway().getName(),
                            transaction.getStatus().getName()
                    )
            );
        }

        return  new PageResponseDto<>(
                listDto,
                transactions.getNumber(),
                transactions.getSize(),
                transactions.getTotalPages(),
                transactions.getTotalElements()
        );
    }

    @Override
    public PageResponseDto<TransactionCustomerResponseDto> getAllTransactionsByCustomer(Integer page, Integer size) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID userId = validateId(principal.getId());

        User requestingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        validatePageAndSize(page, size);

        Pageable pageable =  PageRequest.of((page - 1), size, Sort.by("createdAt").descending());
        Page<Transaction> transactions;
        List<TransactionCustomerResponseDto> listDto = new ArrayList<>();

        if (!requestingUser.getRole().getCode().equals(RoleCode.CUS.name())) {
            throw  new NotAllowedException("Only customer allowed to view transactions");
        }

        transactions = transactionRepository.findAllByCustomer_id(userId, pageable);

        for (Transaction transaction : transactions.getContent()) {
            listDto.add(new TransactionCustomerResponseDto(
                    transaction.getId(),
                    transaction.getCode(),
                    transaction.getNominal(),
                    transaction.getVirtualAccount(),
                    transaction.getProduct().getName(),
                    transaction.getPaymentGateway().getName(),
                    transaction.getStatus().getName()
            ));
        }

        return  new PageResponseDto<>(
                listDto,
                transactions.getNumber(),
                transactions.getSize(),
                transactions.getTotalPages(),
                transactions.getTotalElements()
        );

    }

    @CacheEvict(value = "history", allEntries = true)
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public CreateResponseDto createTransaction(TransactionRequestDto requestDto) {
        AuthorizationPojo principal = principalService.getPrincipal();

        UUID customerId = validateId(principal.getId());
        UUID gateawayId = validateId(requestDto.getPaymentGateawayId());
        UUID productId = validateId(requestDto.getProductId());

        User customer = userRepository.findById(customerId).orElseThrow(
                () -> new NotFoundException("user not found")
        );

        if (!customer.getRole().getCode().equals(RoleCode.CUS.name())) {
            throw new NotAllowedException("not allowed to create transaction");
        }

        PaymentGateaway paymentGateaway = paymentGateawayRepository.findById(gateawayId).orElseThrow(
                () -> new NotFoundException("Payment Gateaway not found")
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found")
        );

        TransactionStatus status = transactionStatusRepository.findByCode(TransactionStatusCode.PROCESSING.name()).orElseThrow(
                () -> new NotFoundException("Transaction Status not found")
        );

        String code = "TRX-"+generatorUtil.generateCode(20);

        Transaction transaction = new Transaction();
        transaction.setCode(code);
        transaction.setCustomer(customer);
        transaction.setPaymentGateway(paymentGateaway);
        transaction.setProduct(product);
        transaction.setStatus(status);
        transaction.setNominal(requestDto.getAmount());
        transaction.setVirtualAccount(requestDto.getVirtualAccount());
        setCreate(transaction);

        Transaction createdTransaction = transactionRepository.save(transaction);

        createTransactionHistory(createdTransaction, status);

        sendTransactionCreatedEmail(customer,  createdTransaction);

        return new CreateResponseDto(createdTransaction.getId(), "Transaction created successfully");

    }

    @CacheEvict(value = "history", allEntries = true)
    @Override
    @Transactional(rollbackOn =  Exception.class)
    public CommonResponseDto updateTransaction(String id, String code) {
        AuthorizationPojo principal = principalService.getPrincipal();

        UUID transactionId = validateId(id);
        UUID gatewayAdminId = validateId(principal.getId());

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new NotFoundException("transaction not found")
        );

        verifyPaymentGatewayAdmin(gatewayAdminId, transaction.getPaymentGateway());

        if (!TransactionStatusCode.PROCESSING.name().equals(transaction.getStatus().getCode())) {
            throw new NotAllowedException("transaction already processed");
        }

        TransactionStatus status = transactionStatusRepository.findByCodeEqualsIgnoreCase((code).trim()).orElseThrow(
                () -> new NotFoundException("transaction status not found")
        );

        transaction.setStatus(status);
        setUpdate(transaction);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        createTransactionHistory(updatedTransaction, status);

        sendTransactionUpdatedEmail(transaction.getCustomer(), updatedTransaction);

        return new CommonResponseDto("Transaction " + updatedTransaction.getStatus().getName());
    }

    private void verifyPaymentGatewayAdmin(UUID adminUserId, PaymentGateaway paymentGateaway) {
        User adminUser = userRepository.findById(adminUserId).orElseThrow(
                () -> new NotFoundException("Admin user not found")
        );

        if (!adminUser.getRole().getCode().equals(RoleCode.PG.name())) {
            throw new NotAllowedException("Only Payment Gateway Admin can approve/reject transactions");
        }

        PaymentGateawayAdmin paymentGatewayAdmin = paymentGateawayAdminRepository.findByGateawayAdminAndPaymentGateaway(
                adminUser, paymentGateaway
        ).orElseThrow(
                () -> new NotAllowedException("User is not a Payment Gateway Admin for this transaction")
        );

        if (!paymentGatewayAdmin.getPaymentGateaway().getId().equals(paymentGateaway.getId())) {
            throw new NotAllowedException("User is not a Payment Gateway Admin");
        }
    }

    private void createTransactionHistory(Transaction transaction, TransactionStatus status) {
        TransactionHistory history = new TransactionHistory();
        history.setTransaction(transaction);
        history.setStatus(status);
        setCreate(history);
        transactionHistoryRepository.save(history);
    }

    private void sendTransactionCreatedEmail(User user, Transaction transaction) {
        String body = mailUtil.buildTransactionCreatedEmail(transaction);

        EmailNotificationDto notificationDto = new EmailNotificationDto();
        notificationDto.setEmail(user.getEmail());
        notificationDto.setSubject("Transaction Journal");
        notificationDto.setBody(body);
        notificationDto.setHtml(true);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_CREATE_TRANSACTION_EX,
                RabbitMQConfig.EMAIL_CREATE_TRANSACTION_KEY,
                notificationDto
        );
    }

    private void sendTransactionUpdatedEmail(User user, Transaction transaction) {
        String body = mailUtil.buildTransactionUpdatedEmail(transaction);

        EmailNotificationDto notificationDto = new EmailNotificationDto();
        notificationDto.setEmail(user.getEmail());
        notificationDto.setSubject("Transaction Journal");
        notificationDto.setBody(body);
        notificationDto.setHtml(true);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_UPDATE_TRANSACTION_EX,
                RabbitMQConfig.EMAIL_UPDATE_TRANSACTION_KEY,
                notificationDto
        );
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_CREATE_TRANSACTION_QUEUE)
    public void receiveTransactionCreatedEmail(EmailNotificationDto notificationDto) {
        mailUtil.sendEmailNotification(notificationDto.getEmail(), notificationDto.getSubject(), notificationDto.getBody(), notificationDto.isHtml());
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_UPDATE_TRANSACTION_QUEUE)
    public void receiveTransactionUpdatedEmail(EmailNotificationDto notificationDto) {
        mailUtil.sendEmailNotification(notificationDto.getEmail(), notificationDto.getSubject(), notificationDto.getBody(), notificationDto.isHtml());
    }

}
