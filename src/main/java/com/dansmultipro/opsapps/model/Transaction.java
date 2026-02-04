package com.dansmultipro.opsapps.model;

import com.dansmultipro.opsapps.baseclass.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@EqualsAndHashCode(callSuper=true)
public class Transaction extends BaseModel {

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private BigDecimal nominal;

    @Column(nullable = false)
    private String virtualAccount;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "payment_gateway_id")
    private PaymentGateaway paymentGateway;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TransactionStatus status;


}
