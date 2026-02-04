package com.dansmultipro.opsapps.repository;

import com.dansmultipro.opsapps.model.PaymentGateaway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentGateawayRepository extends JpaRepository<PaymentGateaway, UUID> {
    Boolean existsByCode(String code);
    Boolean existsByName(String name);
}
