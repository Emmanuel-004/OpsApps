package com.dansmultipro.opsapps.model;

import com.dansmultipro.opsapps.baseclass.BaseModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
public class User extends BaseModel {

    @Column(nullable=false, length = 100)
    private String userName;

    @Column(unique=true,  nullable=false, length = 100)
    private String email;

    @Column(nullable=false, length = 200)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(nullable = false)
    private Boolean isActive;

    @Column
    private String activationCode;
}
