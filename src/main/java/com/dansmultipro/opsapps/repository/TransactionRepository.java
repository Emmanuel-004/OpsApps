package com.dansmultipro.opsapps.repository;

import com.dansmultipro.opsapps.model.Product;
import com.dansmultipro.opsapps.model.Transaction;
import com.dansmultipro.opsapps.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByCustomer_id(UUID userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "INNER JOIN PaymentGateawayAdmin pga ON t.paymentGateway.Id = pga.paymentGateaway.Id " +
            "WHERE pga.gateawayAdmin.Id = :adminId")
    Page<Transaction> findAllByAdminId(@Param("adminId") UUID adminId, Pageable pageable);

    Boolean existsByCustomer(User customer);
    Boolean existsByProduct(Product product);
}
