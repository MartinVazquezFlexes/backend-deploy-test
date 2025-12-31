package com.techforb.apiportalrecruiting.modules.backoffice.user;


import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Builder
@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final transient UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles().stream()
                .map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase())
                )
                .toList();
    }


    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userEntity.getIsAccountNotExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getIsAccountNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userEntity.getIsCredentialNotExpired();
    }

    @Override
    public boolean isEnabled() {
        return userEntity.isEnabled();
    }
}

