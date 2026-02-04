package com.dansmultipro.opsapps.dto.history;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponseDto {
    String code;
    String status;
    String createdDate;
}
