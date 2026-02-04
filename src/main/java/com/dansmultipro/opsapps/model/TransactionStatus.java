package com.dansmultipro.opsapps.model;

import com.dansmultipro.opsapps.baseclass.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper=true)
public class TransactionStatus extends BaseModel {

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

}
