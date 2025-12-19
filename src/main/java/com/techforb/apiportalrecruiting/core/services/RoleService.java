package com.techforb.apiportalrecruiting.core.services;

import com.techforb.apiportalrecruiting.core.entities.Role;

public interface RoleService {
    Role findByName (String roleName);
    void assignRoleToUser (String roleName, String email);
    
}
