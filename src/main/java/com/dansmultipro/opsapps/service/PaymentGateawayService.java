package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayRequestDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.UpdatePaymentGateawayRequestDto;

import java.util.List;

public interface PaymentGateawayService {
    List<PaymentGateawayResponseDto> findAllPaymentGateaway();
    PaymentGateawayResponseDto findById(String id);
    CreateResponseDto create(PaymentGateawayRequestDto requestDto);
    UpdateResponseDto update(String id, UpdatePaymentGateawayRequestDto requestDto);
    DeleteResponseDto delete(String id);
}
