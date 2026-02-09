package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.CommonResponseDto;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionCustomerResponseDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionRequestDto;
import com.dansmultipro.opsapps.dto.transaction.TransactionResponseDto;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final PrincipalService principalService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'PG')")
    public ResponseEntity<PageResponseDto<TransactionResponseDto>> getAllTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        AuthorizationPojo principal = principalService.getPrincipal();
        String id = principal.getId();
        String roleCode = principal.getRoleCode();

        PageResponseDto<TransactionResponseDto> transactions = transactionService.getAllTransactions(page, size, id, roleCode);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('CUS')")
    public ResponseEntity<PageResponseDto<TransactionCustomerResponseDto>> getAllCustomerTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        AuthorizationPojo principal = principalService.getPrincipal();
        String id = principal.getId();
        String roleCode = principal.getRoleCode();

        PageResponseDto<TransactionCustomerResponseDto> transactions = transactionService.getAllTransactionsByCustomer(page, size,  id, roleCode);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('CUS')")
    public ResponseEntity<CreateResponseDto> createTransaction(@RequestBody @Valid TransactionRequestDto requestDto) {
        CreateResponseDto response = transactionService.createTransaction(requestDto);
        return new  ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/{statusCode}")
    @PreAuthorize("hasAuthority('PG')")
    public ResponseEntity<CommonResponseDto> updateTransactionStatus(@PathVariable String id,@PathVariable String statusCode) {
        CommonResponseDto response = transactionService.updateTransaction(id, statusCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
