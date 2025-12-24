package com.techforb.apiportalrecruiting.core.repositories;

import com.techforb.apiportalrecruiting.core.entities.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_DEFAULT");
        em.persist(role);
        em.flush();
    }
    @Test
    void findByName() {
        Role foundRole = roleRepository.findByName(role.getName()).orElse(null);
        assertNotNull(foundRole);
        assertEquals(role.getName(), foundRole.getName());
    }
}