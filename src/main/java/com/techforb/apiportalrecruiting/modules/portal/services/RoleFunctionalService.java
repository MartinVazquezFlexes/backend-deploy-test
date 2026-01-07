package com.techforb.apiportalrecruiting.modules.portal.services;

import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;

import java.util.List;

public interface RoleFunctionalService {

    List<RoleFunctional>getAllRolesFunctional();

    RoleFunctional saveRoleFunctional(Long roleFunctionalId,String email);

    RoleFunctional findById(Long id);
    void assignRoleFunctional(Person person, Long roleFunctionalId);
}
