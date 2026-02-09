package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.pojo.AuthorizationPojo;

import java.util.UUID;

public interface PrincipalService {
    AuthorizationPojo getPrincipal();
    String getId(UUID id, String roleCode);
}
