package net.javaguides.common_lib.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    // Getters và Setters
    private LocalDateTime timestamp;
    private T data;
    private int statusCode;

    public ApiResponse(T data, int statusCode) {
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.statusCode = statusCode;
    }

}
