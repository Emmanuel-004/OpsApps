package com.dansmultipro.opsapps.repository;

import com.dansmultipro.opsapps.model.TransactionHistory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID>, JpaSpecificationExecutor<TransactionHistory> {

    @Query("SELECT th FROM TransactionHistory th " +
            "INNER JOIN PaymentGateawayAdmin pga ON th.transaction.paymentGateway.id = pga.paymentGateaway.id " +
            "WHERE pga.gateawayAdmin.id = :adminId")
    Page<TransactionHistory> findAllByPaymentGateawayAdminId(@Param("adminId") UUID adminId, Pageable pageable);

    @Query("SELECT th FROM TransactionHistory th " +
            "WHERE th.transaction.customer.id = :customerId")
    Page<TransactionHistory> findAllByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
}
