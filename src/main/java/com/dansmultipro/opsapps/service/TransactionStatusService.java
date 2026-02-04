package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.transactionstatus.TransactionStatusResponseDto;

import java.util.List;

public interface TransactionStatusService {
    List<TransactionStatusResponseDto> getAll();
}
