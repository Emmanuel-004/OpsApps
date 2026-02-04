package com.dansmultipro.opsapps.dto.email;

import lombok.Data;

@Data
public class EmailNotificationDto {
    String subject;
    String body;
    String email;
    boolean isHtml;
}
