package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.history.TransactionHistoryResponseDto;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.model.TransactionHistory;
import com.dansmultipro.opsapps.repository.TransactionHistoryRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.TransactionHistoryService;
import com.dansmultipro.opsapps.specification.TransactionHistorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl extends BaseService implements TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "history", key = "'page:'+#page+'size:'+#size+'userId:'+#userId+'roleCode:'+#roleCode")
    @Override
    public PageResponseDto<TransactionHistoryResponseDto> getAllHistories(Integer page, Integer size, String userId, String roleCode) {

        UUID id = validateId(userId);

        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        validatePageAndSize(page, size);

        Specification<TransactionHistory> spec = Specification.allOf(
                TransactionHistorySpecification.filterByRoleAndUser(roleCode, id),
                TransactionHistorySpecification.orderByCreatedDate()
        );

        Pageable pageable = PageRequest.of((page-1), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TransactionHistory> transactionHistories = transactionHistoryRepository.findAll(spec, pageable);
        List<TransactionHistoryResponseDto> data = new ArrayList<>();
        

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
