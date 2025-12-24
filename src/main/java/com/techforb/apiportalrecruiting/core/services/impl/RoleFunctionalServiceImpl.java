package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.RoleFunctionalRepository;
import com.techforb.apiportalrecruiting.core.services.RoleFunctionalService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleFunctionalServiceImpl implements RoleFunctionalService {

    private final RoleFunctionalRepository roleFunctionalRepository;
    private final LocalizedMessageService localizedMessageService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    @Override
    public List<RoleFunctional> getAllRolesFunctional() {
    try {
            return roleFunctionalRepository.findAll();
        } catch (DataAccessException e) {
        log.error("Error fetching functional roles", e);
        throw new RuntimeException(localizedMessageService.getMessage("error.fetching.functional.roles"), e);
    }
    }

    @Override
    @Transactional
    public RoleFunctional saveRoleFunctional(Long roleFunctionalId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("user.not_found")));

        Person person = user.getPerson();
        if (person == null) {
            throw new RuntimeException(localizedMessageService.getMessage("person.not_found"));
        }

        RoleFunctional roleFunctional = roleFunctionalRepository.findById(roleFunctionalId)
                .orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("functional.role.not_found", roleFunctionalId)));

        try {
            List<RoleFunctional> newRoles = new ArrayList<>();
            newRoles.add(roleFunctional);
            person.setRoleFunctionals(newRoles);
            personRepository.save(person);
            return roleFunctional;
        } catch (DataAccessException e) {
            log.error("Error saving functional role relation for person", e);
            throw new RuntimeException(localizedMessageService.getMessage("error.saving.functional.role"), e);
        }
    }
}
