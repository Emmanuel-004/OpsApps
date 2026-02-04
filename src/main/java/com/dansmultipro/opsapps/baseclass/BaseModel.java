package com.dansmultipro.opsapps.baseclass;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
public class BaseModel {
    @Id
    @Column(length = 36)
    private UUID Id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private UUID createdBy;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private UUID updatedBy;

    @Version
    @Column
    private Integer version;
}
