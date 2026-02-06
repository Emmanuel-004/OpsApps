package com.dansmultipro.opsapps.baseclass;

import com.dansmultipro.opsapps.exception.InvalidFormatException;
import com.dansmultipro.opsapps.exception.NotAllowedException;
import com.dansmultipro.opsapps.exception.NotFoundException;
import com.dansmultipro.opsapps.repository.UserRepository;
import com.dansmultipro.opsapps.service.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseService {

    protected PrincipalService principalService;

    private UserRepository userRepository;

    @Autowired
    public void setPrincipalService(PrincipalService principalService) {
        this.principalService = principalService;
    }

    @Autowired
    public void  setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public <T extends BaseModel> void setCreate(T model){
        model.setId((UUID.randomUUID()));
        model.setCreatedAt(LocalDateTime.now());
        model.setCreatedBy(validateId(principalService.getPrincipal().getId()));
    }

    public <T extends BaseModel> void setInitialCreate(T model, String roleCode){
        UUID systemId = userRepository.findByRole_Code(roleCode).orElseThrow(
                () -> new NotFoundException("role not found")
        ).getId();

        model.setId((UUID.randomUUID()));
        model.setCreatedAt(LocalDateTime.now());
        model.setCreatedBy(systemId);
    }

    public <T extends BaseModel> void setUpdate(T model) {
        model.setUpdatedAt(LocalDateTime.now());
        model.setUpdatedBy(validateId(principalService.getPrincipal().getId()));

    }

    protected UUID validateId(String id) {
        if (id == null || id.isBlank()){
            throw new InvalidFormatException("id cannot be null or empty");
        }

        try {
            UUID result = UUID.fromString(id);
            return result;

        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException("Invalid UUID format");

        }
    }

    protected void validatePageAndSize(Integer page, Integer size) {
        if (page < 1){
            throw new InvalidFormatException("page cannot be less than 1");
        }

        if (size < 1){
            throw new NotAllowedException("size cannot be less than 1");
        }
    }
}
