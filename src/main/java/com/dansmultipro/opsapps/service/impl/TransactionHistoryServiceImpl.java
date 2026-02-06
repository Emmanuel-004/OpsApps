package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.history.TransactionHistoryResponseDto;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.model.TransactionHistory;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.TransactionHistoryRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl extends BaseService implements TransactionHistoryService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Cacheable(value = "history", key = "'page:'+#page+'size:'+#size")
    @Override
    public PageResponseDto<TransactionHistoryResponseDto> getAllHistories(Integer page, Integer size) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID userId = validateId(principal.getId());

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        validatePageAndSize(page, size);

        Pageable pageable = PageRequest.of((page-1), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TransactionHistory> transactionHistories;
        List<TransactionHistoryResponseDto> data = new ArrayList<>();

        if (user.getRole().getCode().equals(RoleCode.SA.name())) {

            transactionHistories = transactionHistoryRepository.findAll(pageable);

        } else if (user.getRole().getCode().equals(RoleCode.PG.name())) {

            transactionHistories = transactionHistoryRepository.findAllByPaymentGateawayAdminId(userId, pageable);

        } else if (user.getRole().getCode().equals(RoleCode.CUS.name())) {

            transactionHistories = transactionHistoryRepository.findAllByCustomerId(userId, pageable);

        } else {
            throw new NotAllowedException("User not authorized");
        }

        for (TransactionHistory transactionHistory : transactionHistories.getContent()) {
            data.add( new  TransactionHistoryResponseDto(
                    transactionHistory.getTransaction().getCode(),
                    transactionHistory.getStatus().getName(),
                    transactionHistory.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            ));
        }

        return new PageResponseDto<>(
                data,
                transactionHistories.getNumber(),
                transactionHistories.getSize(),
                transactionHistories.getTotalPages(),
                transactionHistories.getTotalElements()
        );

    }
}
