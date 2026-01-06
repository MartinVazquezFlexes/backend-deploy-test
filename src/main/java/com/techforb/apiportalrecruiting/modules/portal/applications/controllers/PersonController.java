package com.techforb.apiportalrecruiting.modules.portal.applications.controllers;

import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonResponseDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonUpdateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.person.PersonProfileResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/portal/person/")
@AllArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping("/profile")
    public ResponseEntity<PersonProfileResponseDTO> getPersonProfile() {
        return ResponseEntity.ok(personService.getPersonProfile());
    }
    
    @PutMapping("/profile")
    public ResponseEntity<PersonResponseDTO> updatePersonProfile(
        @RequestBody @Valid PersonUpdateDTO updateDTO
    ) {
        return ResponseEntity.ok(personService.updatePersonProfile(updateDTO));
    }
}
