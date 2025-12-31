package com.techforb.apiportalrecruiting.core.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Role;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.RoleRepository;
import com.techforb.apiportalrecruiting.core.services.RoleService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final LocalizedMessageService localizedMessageService;
    private final UserRepository userRepository;

    @Override
    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NoSuchElementException(this.localizedMessageService.getMessage("role.not_found")));
    }

    @Override
    public void assignRoleToUser(String roleName, String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Role role = this.findByName(roleName);
            if (role == null) {
                throw new NoSuchElementException(this.localizedMessageService.getMessage("role.not_found"));
            }

            // limpio roles existentes y asigno el nuevo
            user.get().getRoles().clear();
            user.get().getRoles().add(role);

            userRepository.save(user.get());
        } else {
            throw new NoSuchElementException(this.localizedMessageService.getMessage("user.not_found"));
        }
    }
}
