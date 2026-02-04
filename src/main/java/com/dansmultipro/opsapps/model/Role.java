package com.dansmultipro.opsapps.model;

import com.dansmultipro.opsapps.baseclass.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper=true)
public class Role extends BaseModel {

    @Column(unique=true,  nullable=false, length = 32)
    private String name;

    @Column(unique=true,  nullable=false, length = 20)
    private String code;
}
