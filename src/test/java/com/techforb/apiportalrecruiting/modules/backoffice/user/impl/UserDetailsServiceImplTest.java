package com.techforb.apiportalrecruiting.modules.backoffice.user.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private LocalizedMessageService localizedMessageService;

    /*@BeforeEach
    void setUp(){

    }*/

    @Test
    void loadUserByUsername_ShouldReturnUser() {
        String email = "test@test.com";
        UserEntity user = new UserEntity(); user.setId(10L); user.setEmail(email);

        when(userRepository.findByEmail("test@test.com")).thenReturn(java.util.Optional.of(user));
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@test.com");

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void loadUserByUsername_ShouldReturnUsernameNotFound() {
        String email = "testerror@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        verify(userRepository, times(1)).findByEmail(email);
    }
}