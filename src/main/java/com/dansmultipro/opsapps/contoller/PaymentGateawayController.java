package com.dansmultipro.opsapps.contoller;

import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayRequestDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.UpdatePaymentGateawayRequestDto;
import com.dansmultipro.opsapps.service.PaymentGateawayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/payment-gateaway")
@RequiredArgsConstructor
public class PaymentGateawayController {

    private final PaymentGateawayService paymentGateawayService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SA', 'PG', 'CUS')")
    public ResponseEntity<List<PaymentGateawayResponseDto>>  getAllPaymentGateaway(){
        List<PaymentGateawayResponseDto> response = paymentGateawayService.findAllPaymentGateaway();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SA', 'PG', 'CUS')")
    public ResponseEntity<PaymentGateawayResponseDto> findPaymentGateawayById(@PathVariable String id){
        PaymentGateawayResponseDto responseDto = paymentGateawayService.findById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<CreateResponseDto> create(@RequestBody @Valid PaymentGateawayRequestDto requestDto){
        CreateResponseDto createResponseDto = paymentGateawayService.create(requestDto);
        return new ResponseEntity<>(createResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<UpdateResponseDto> update(@PathVariable String id, @RequestBody @Valid UpdatePaymentGateawayRequestDto requestDto){
        UpdateResponseDto response = paymentGateawayService.update(id, requestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SA')")
    public ResponseEntity<DeleteResponseDto> delete(@PathVariable String id){
        DeleteResponseDto deleteResponseDto = paymentGateawayService.delete(id);
        return new ResponseEntity<>(deleteResponseDto, HttpStatus.OK);
    }
}
