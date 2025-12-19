package com.techforb.apiportalrecruiting.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {
    private String timestamp;
    private Integer status;
    private String error;
    private String message;
}
