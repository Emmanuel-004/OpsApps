package com.dansmultipro.opsapps.service.impl;

import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.pojo.AuthorizationPojo;
import com.dansmultipro.opsapps.service.PrincipalService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PrincipalServiceImpl implements PrincipalService {
    @Override
    public AuthorizationPojo getPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null){
            throw new NotFoundException("Invaild login");
        }
        return (AuthorizationPojo) auth.getPrincipal();
    }
}
