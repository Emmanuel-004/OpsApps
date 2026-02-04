package com.dansmultipro.opsapps.model;

import com.dansmultipro.opsapps.baseclass.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class PaymentGateawayAdmin extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "payment_gateaway_id")
    private PaymentGateaway paymentGateaway;

    @OneToOne
    @JoinColumn(name = "gateway_admin_id")
    private User gateawayAdmin;
}
