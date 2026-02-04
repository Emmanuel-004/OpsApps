package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.history.TransactionHistoryResponseDto;
import com.dansmultipro.opsapps.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transactions")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @GetMapping("/histories")
    public ResponseEntity<PageResponseDto<TransactionHistoryResponseDto>> findAllHistories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        PageResponseDto<TransactionHistoryResponseDto> histories = transactionHistoryService.getAllHistories(page, size);
        return new  ResponseEntity<>(histories, HttpStatus.OK);
    }

}
