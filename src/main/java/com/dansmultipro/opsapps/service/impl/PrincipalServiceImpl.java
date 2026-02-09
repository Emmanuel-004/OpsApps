package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.constant.RoleCode;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.model.PaymentGateawayAdmin;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.repository.PaymentGateawayAdminRepository;
import com.dansmultipro.opsapps.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalServiceImpl implements PrincipalService {

    private final PaymentGateawayAdminRepository paymentGateawayAdminRepository;

    @Override
    public AuthorizationPojo getPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null){
            throw new NotFoundException("Invaild login");
        }
        return (AuthorizationPojo) auth.getPrincipal();
    }

    @Override
    public String getId(UUID id, String roleCode) {
        if (roleCode.equals(RoleCode.PG.name())) {
            PaymentGateawayAdmin admin = paymentGateawayAdminRepository.findByGateawayAdminId(id).orElseThrow(
                    () -> new NotFoundException("admin not found")
            );
            return admin.getPaymentGateaway().getId().toString();
        }
        return id.toString();
    }
}
