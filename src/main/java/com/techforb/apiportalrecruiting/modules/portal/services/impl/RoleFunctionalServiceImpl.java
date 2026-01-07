package com.techforb.apiportalrecruiting.modules.portal.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.RoleFunctionalRepository;
import com.techforb.apiportalrecruiting.modules.portal.services.RoleFunctionalService;
import com.techforb.apiportalrecruiting.core.services.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.techforb.apiportalrecruiting.core.exceptions.RoleFunctionalFetchException;
import com.techforb.apiportalrecruiting.core.exceptions.ResourceNotFoundException;
import com.techforb.apiportalrecruiting.core.exceptions.RoleFunctionalSaveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleFunctionalServiceImpl implements RoleFunctionalService {

    private final RoleFunctionalRepository roleFunctionalRepository;
    private final LocalizedMessageService localizedMessageService;
    private final UserService userService;
    private final PersonRepository personRepository;

    @Override
    public List<RoleFunctional> getAllRolesFunctional() {
        try {
            return roleFunctionalRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Error fetching functional roles", e);
            throw new RoleFunctionalFetchException(
                    localizedMessageService.getMessage("error.fetching.functional.roles"),
                    e
            );
        }
    }

    @Override
    @Transactional
    public RoleFunctional saveRoleFunctional(Long roleFunctionalId, String email) {
        Optional<UserEntity> user = userService.findByEmail(email);

        Person person = user.get().getPerson();
        if (person == null) {
            throw new ResourceNotFoundException(
                    localizedMessageService.getMessage("person.not_found")
            );
        }

        RoleFunctional roleFunctional = roleFunctionalRepository.findById(roleFunctionalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        localizedMessageService.getMessage("functional.role.not_found", roleFunctionalId)
                ));

        try {
            person.setRoleFunctionals(List.of(roleFunctional)); // inmutable, más limpio
            personRepository.save(person);
            return roleFunctional;
        } catch (DataAccessException e) {
            log.error("Error saving functional role relation for person", e);
            throw new RoleFunctionalSaveException(
                    localizedMessageService.getMessage("error.saving.functional.role"),
                    e
            );
        }
    }


    @Override
    public RoleFunctional findById(Long id) {
        return roleFunctionalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(localizedMessageService.getMessage("functional.role.not_found", id)));
    }

    @Override
    public void assignRoleFunctional(Person person, Long roleFunctionalId) {
        RoleFunctional roleFunctional = roleFunctionalRepository.findById(roleFunctionalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        localizedMessageService.getMessage("functional.role.not_found", roleFunctionalId)
                ));

        if (person.getRoleFunctionals() == null) {
            person.setRoleFunctionals(new ArrayList<>());
        }

        person.getRoleFunctionals().clear();
        person.getRoleFunctionals().add(roleFunctional);
    }
}
