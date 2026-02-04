package com.dansmultipro.opsapps.repository;

import com.dansmultipro.opsapps.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionStatusRepository extends JpaRepository<TransactionStatus, UUID> {
    Optional<TransactionStatus> findByCode(String code);
    Optional<TransactionStatus>findByCodeEqualsIgnoreCase(String code);
}
