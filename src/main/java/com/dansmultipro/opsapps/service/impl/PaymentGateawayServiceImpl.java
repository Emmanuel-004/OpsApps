package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.baseclass.BaseService;
import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.dto.CreateResponseDto;
import com.dansmultipro.opsapps.dto.DeleteResponseDto;
import com.dansmultipro.opsapps.dto.UpdateResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayRequestDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.PaymentGateawayResponseDto;
import com.dansmultipro.opsapps.dto.paymentgateaway.UpdatePaymentGateawayRequestDto;
import com.dansmultipro.opsapps.exception.DataIntegrationException;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.model.PaymentGateaway;
import com.dansmultipro.opsapps.model.User;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.PaymentGateawayRepository;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.PaymentGateawayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentGateawayServiceImpl extends BaseService implements PaymentGateawayService {

    private final PaymentGateawayRepository paymentGateawayRepository;
    private final UserRepository userRepository;

    @Override
    public List<PaymentGateawayResponseDto> findAllPaymentGateaway() {
        return paymentGateawayRepository.findAll()
                .stream()
                .map(pg -> new PaymentGateawayResponseDto(
                        pg.getId(),
                        pg.getName(),
                        pg.getCode()
                ))
                .toList();
    }

    @Override
    public PaymentGateawayResponseDto findById(String id) {
        UUID pgId = validateId(id);
        PaymentGateaway pg = paymentGateawayRepository.findById(pgId).orElseThrow(
                () -> new NotFoundException("Payment Gateaway not found")
        );

        return new PaymentGateawayResponseDto(
                pg.getId(),
                pg.getName(),
                pg.getCode()
        );
    }

    @Override
    public CreateResponseDto create(PaymentGateawayRequestDto requestDto) {

        if (paymentGateawayRepository.existsByCode(requestDto.getCode())) {
            throw new NotAllowedException("Payment Gateaway code already exists");
        }

        if (paymentGateawayRepository.existsByName(requestDto.getName())) {
            throw new NotAllowedException("Payment Gateaway name already exists");
        }

        PaymentGateaway pg = new PaymentGateaway();
        pg.setName(requestDto.getName());
        pg.setCode(requestDto.getCode());
        setCreate(pg);

        PaymentGateaway createdPg =  paymentGateawayRepository.save(pg);
        return new CreateResponseDto(createdPg.getId(), "Payment Gateaway created");
    }

    @Override
    public UpdateResponseDto update(String id, UpdatePaymentGateawayRequestDto requestDto) {
        UUID pgId = validateId(id);

        PaymentGateaway existingPg = paymentGateawayRepository.findById(pgId).orElseThrow(
                () -> new NotFoundException("Payment Gateaway not found")
        );

        if (!existingPg.getVersion().equals(requestDto.getVersion())) {
            throw new DataIntegrationException("version mismatch");
        }

        if (!existingPg.getName().equals(requestDto.getName())) {
            if (paymentGateawayRepository.existsByName(requestDto.getName())) {
                throw new NotAllowedException("Payment Gateaway name already exists");
            }
        }

        if (!existingPg.getCode().equals(requestDto.getCode())) {
            if (paymentGateawayRepository.existsByCode(requestDto.getCode())) {
                throw new NotAllowedException("Payment Gateaway code already exists");
            }
        }

        existingPg.setName(requestDto.getName());
        existingPg.setCode(requestDto.getCode());
        setUpdate(existingPg);

        PaymentGateaway updatedPg = paymentGateawayRepository.save(existingPg);

        return new UpdateResponseDto(updatedPg.getVersion(), "Payment Gateaway updated");
    }

    @Override
    public DeleteResponseDto delete(String id) {
        UUID pgId = validateId(id);

        PaymentGateaway existingPg = paymentGateawayRepository.findById(pgId).orElseThrow(
                () -> new NotFoundException("Payment Gateaway not found")
        );

        paymentGateawayRepository.delete(existingPg);

        return new DeleteResponseDto("Payment Gateaway deleted");
    }
}
