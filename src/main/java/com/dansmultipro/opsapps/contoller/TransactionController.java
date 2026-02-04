package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionCustomerResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionRequestDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionResponseDto;
import com.dansmultipro.opsapps.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PageResponseDto<TransactionResponseDto>> getAllTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        PageResponseDto<TransactionResponseDto> transactions = transactionService.getAllTransactions(page, size);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<PageResponseDto<TransactionCustomerResponseDto>> getAllCustomerTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        PageResponseDto<TransactionCustomerResponseDto> transactions = transactionService.getAllTransactionsByCustomer(page, size);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<CreateResponseDto> createTransaction(@RequestBody @Valid TransactionRequestDto requestDto) {
        CreateResponseDto response = transactionService.createTransaction(requestDto);
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/{statusCode}")
    public ResponseEntity<CommonResponseDto> updateTransactionStatus(@PathVariable String id,@PathVariable String statusCode) {
        CommonResponseDto response = transactionService.updateTransaction(id, statusCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
