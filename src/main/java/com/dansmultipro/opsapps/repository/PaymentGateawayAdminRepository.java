package com.dansmultipro.opsapps.repository;

import com.dansmultipro.opsapps.model.PaymentGateaway;
import com.dansmultipro.opsapps.model.PaymentGateawayAdmin;
import com.dansmultipro.opsapps.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentGateawayAdminRepository extends JpaRepository<PaymentGateawayAdmin, UUID> {
    Optional<PaymentGateawayAdmin> findByGateawayAdmin(User gatewayAdmin);
    Optional<PaymentGateawayAdmin> findByGateawayAdminId(UUID gateawayAdminId);
    Optional<PaymentGateawayAdmin> findByGateawayAdminIdAndPaymentGateawayId(UUID adminId, UUID gateawayId);
}
