package com.techforb.apiportalrecruiting.modules.backoffice.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkedInCallbackRequestDTO {
    private String code;
    private String state;
} 