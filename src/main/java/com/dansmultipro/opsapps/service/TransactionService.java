package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionCustomerResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionRequestDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionResponseDto;

public interface TransactionService {
    PageResponseDto<TransactionResponseDto> getAllTransactions(Integer page, Integer size, String userId, String roleCode);
    PageResponseDto<TransactionCustomerResponseDto>getAllTransactionsByCustomer(Integer page, Integer size, String userId, String roleCode);
    CreateResponseDto createTransaction(TransactionRequestDto requestDto);
    CommonResponseDto updateTransaction(String id, String code);
}
