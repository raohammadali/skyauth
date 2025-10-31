package com.skynetauth.auth_service.dto.dto;

import lombok.Data;

@Data
public class LogMessageDto {
    private String level;
    private String message;

    /**
 * Creates a new LogMessageDto with no initial field values.
 *
 * <p>The `level` and `message` fields are unset and must be initialized via setters or the all-arguments constructor.</p>
 */
public LogMessageDto() {}

    /**
     * Create a LogMessageDto with the specified log level and message.
     *
     * @param level   the log severity label (for example, "INFO", "WARN", "ERROR")
     * @param message the log message text
     */
    public LogMessageDto(String level, String message) {
        this.level = level;
        this.message = message;
    }
}
