package com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonUpdateDTO {
    // Datos básicos
    private String firstName;
    private String lastName;
    private String phoneNumber;
    
    // País de residencia
    private Long countryId;
    
    // Roles funcionales
    private Long functionalRoleId;
    
    // Idiomas con nivel
    private Long languageId;
    
    // Skills
    private List<Long> skillIds;
    
    // Contactos (redes sociales)
    private List<ContactUpdateDTO> contacts;
}
