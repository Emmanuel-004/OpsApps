package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.PageResponseDto;
import com.dansmultipro.opsapps.dto.history.TransactionHistoryResponseDto;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.service.PrincipalService;
import com.dansmultipro.opsapps.service.TransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/transactions")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;
    private final PrincipalService principalService;

    @GetMapping("/histories")
    @PreAuthorize("hasAnyAuthority('SA', 'PG', 'CUS')")
    public ResponseEntity<PageResponseDto<TransactionHistoryResponseDto>> findAllHistories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        AuthorizationPojo principal = principalService.getPrincipal();
        UUID userId = UUID.fromString(principal.getId());
        String roleCode = principal.getRoleCode();
        String id = principalService.getId(userId, roleCode);

        PageResponseDto<TransactionHistoryResponseDto> histories = transactionHistoryService.getAllHistories(page, size, id, roleCode);
        return new  ResponseEntity<>(histories, HttpStatus.OK);
    }

}
