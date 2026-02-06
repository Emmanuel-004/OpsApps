package com.dansmultipro.opsapps.dto.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponseDto {
    String code;
    String status;
    String createdDate;
}
