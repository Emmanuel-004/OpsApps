package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.history.TransactionHistoryResponseDto;

public interface TransactionHistoryService {
    PageResponseDto<TransactionHistoryResponseDto> getAllHistories(Integer page, Integer size, String userId, String roleCode);
}
