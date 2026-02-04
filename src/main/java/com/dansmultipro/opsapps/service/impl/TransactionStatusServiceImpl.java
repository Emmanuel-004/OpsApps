package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.dto.transactionstatus.TransactionStatusResponseDto;
import com.dansmultipro.opsapps.repository.TransactionStatusRepository;
import com.dansmultipro.opsapps.service.TransactionStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionStatusServiceImpl extends BaseService implements TransactionStatusService {

    private final TransactionStatusRepository statusRepository;

    @Override
    public List<TransactionStatusResponseDto> getAll() {

        return statusRepository.findAll().stream()
                .map(ts -> new TransactionStatusResponseDto(
                        ts.getId(),
                        ts.getName(),
                        ts.getCode()
                ))
                .collect(Collectors.toList());
    }
}
