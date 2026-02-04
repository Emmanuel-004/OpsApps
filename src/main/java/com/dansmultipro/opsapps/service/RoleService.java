package com.dansmultipro.opsapps.service;

import com.dansmultipro.opsapps.dto.role.RoleResponseDto;

import java.util.List;

public interface RoleService {
    RoleResponseDto findRoleById(String id);
    List<RoleResponseDto> findAllRoles();
}
