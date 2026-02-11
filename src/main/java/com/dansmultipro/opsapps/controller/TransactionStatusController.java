package com.dansmultipro.opsapps.controller;

import com.dansmultipro.opsapps.dto.transactionstatus.TransactionStatusResponseDto;
import com.dansmultipro.opsapps.service.TransactionStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/history-status")
@RequiredArgsConstructor
public class TransactionStatusController {

    private final TransactionStatusService transactionStatusService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'PG', 'CUS')")
    public ResponseEntity<List<TransactionStatusResponseDto>> getTransactionStatus(){
        List<TransactionStatusResponseDto> response = transactionStatusService.getAll();
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }
}
