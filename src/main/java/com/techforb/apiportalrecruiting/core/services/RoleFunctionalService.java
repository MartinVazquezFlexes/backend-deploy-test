package com.techforb.apiportalrecruiting.core.services;

import com.techforb.apiportalrecruiting.core.entities.RoleFunctional;

import java.util.List;

public interface RoleFunctionalService {

    List<RoleFunctional>getAllRolesFunctional();

    RoleFunctional saveRoleFunctional(Long roleFunctionalId,String email);
}
