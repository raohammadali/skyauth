package com.skynetauth.auth_service.dto.dto;

import lombok.Data;

@Data
public class LogMessageDto {
    private String level;
    private String message;

    public LogMessageDto() {}

    public LogMessageDto(String level, String message) {
        this.level = level;
        this.message = message;
    }
}
