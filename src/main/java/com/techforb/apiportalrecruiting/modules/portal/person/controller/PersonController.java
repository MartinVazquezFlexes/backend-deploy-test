package com.techforb.apiportalrecruiting.modules.portal.person.controller;

import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonRequestDTO;
import com.techforb.apiportalrecruiting.modules.portal.person.dto.PersonResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/portal/person/")
@AllArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping("{id}")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Long id){
        return ResponseEntity.ok(personService.getPersonByIdDTO(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<PersonResponseDTO> updatePerson(@PathVariable Long id, @RequestBody PersonRequestDTO personRequestDTO){
        return ResponseEntity.ok(personService.updatePerson(id, personRequestDTO));
    }
}
