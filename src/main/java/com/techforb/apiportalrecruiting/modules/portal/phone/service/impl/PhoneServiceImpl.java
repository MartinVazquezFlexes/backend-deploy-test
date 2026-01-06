package com.techforb.apiportalrecruiting.modules.portal.phone.service.impl;

import com.techforb.apiportalrecruiting.core.entities.Phone;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.modules.portal.phone.dto.PhoneDTO;
import com.techforb.apiportalrecruiting.modules.portal.phone.repository.PhoneRepository;
import com.techforb.apiportalrecruiting.modules.portal.phone.service.PhoneService;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {
    
    private final PhoneRepository phoneRepository;
    private final PersonRepository personRepository;
    
    @Override
    @Transactional
    public Phone updatePhoneForPerson(Long personId, String phoneNumber) {
        // Eliminar teléfonos existentes
        deletePhoneByPersonId(personId);
        
        // Crear nuevo teléfono
        Person person = personRepository.findById(personId)
            .orElseThrow(() -> new RuntimeException("Person not found"));
            
        Phone phone = Phone.builder()
            .person(person)
            .phoneNumber(phoneNumber)
            .build();
            
        return phoneRepository.save(phone);
    }
    
    @Override
    public PhoneDTO getPhoneByPersonId(Long personId) {
        Optional<Phone> phone = phoneRepository.findByPersonId(personId);
        return phone.map(p -> PhoneDTO.builder()
            .id(p.getId())
            .phoneNumber(p.getPhoneNumber())
            .build())
            .orElse(null);
    }
    
    @Override
    @Transactional
    public void deletePhoneByPersonId(Long personId) {
        phoneRepository.deleteByPersonId(personId);
    }
}
