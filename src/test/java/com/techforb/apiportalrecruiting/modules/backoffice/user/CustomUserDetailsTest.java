package com.techforb.apiportalrecruiting.modules.backoffice.user;

import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import org.junit.jupiter.api.Test;

import com.techforb.apiportalrecruiting.core.entities.Role;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void shouldReturnUsernameAndPasswordFromUserEntity() {
        // given
        UserEntity user = new UserEntity();
        user.setEmail("test@email.com");
        user.setPassword("secret");

        CustomUserDetails details = CustomUserDetails.builder()
                .userEntity(user)
                .build();

        // then
        assertEquals("test@email.com", details.getUsername());
        assertEquals("secret", details.getPassword());
    }

    @Test
    void shouldReturnAccountFlagsFromUserEntityWhenAllTrue() {
        // given
        UserEntity user = new UserEntity();
        user.setIsAccountNotExpired(true);
        user.setIsAccountNotLocked(true);
        user.setIsCredentialNotExpired(true);
        user.setEnabled(true);

        CustomUserDetails details = CustomUserDetails.builder()
                .userEntity(user)
                .build();

        // then
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }

    @Test
    void shouldReturnAccountFlagsFromUserEntityWhenAllFalse() {
        // given
        UserEntity user = new UserEntity();
        user.setIsAccountNotExpired(false);
        user.setIsAccountNotLocked(false);
        user.setIsCredentialNotExpired(false);
        user.setEnabled(false);

        CustomUserDetails details = CustomUserDetails.builder()
                .userEntity(user)
                .build();

        // then
        assertFalse(details.isAccountNonExpired());
        assertFalse(details.isAccountNonLocked());
        assertFalse(details.isCredentialsNonExpired());
        assertFalse(details.isEnabled());
    }

    @Test
    void getAuthoritiesShouldMapRolesToRolePrefixAndUppercase() {
        // given
        UserEntity user = new UserEntity();

        Role admin = new Role();
        admin.setName("admin");

        Role recruiter = new Role();
        recruiter.setName("recruiter");

        user.setRoles(java.util.List.of(admin, recruiter));

        CustomUserDetails details = CustomUserDetails.builder()
                .userEntity(user)
                .build();

        // when
        java.util.Set<String> authorities = details.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(java.util.stream.Collectors.toSet());

        // then
        org.junit.jupiter.api.Assertions.assertEquals(2, authorities.size());
        org.junit.jupiter.api.Assertions.assertTrue(authorities.contains("ROLE_ADMIN"));
        org.junit.jupiter.api.Assertions.assertTrue(authorities.contains("ROLE_RECRUITER"));
    }

}
