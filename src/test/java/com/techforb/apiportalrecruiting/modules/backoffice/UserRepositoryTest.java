package com.techforb.apiportalrecruiting.modules.backoffice;

import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenFindByEmailThenReturnUser() {
        UserEntity user = new UserEntity();
        user.setEmail("test@email.com");
        userRepository.save(user);

        Optional<UserEntity> dbUser = userRepository.findByEmail(user.getEmail());

        assertThat(dbUser).isPresent();
        assertThat(dbUser.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void whenFindByNonExistentEmailThenReturnEmpty() {
        Optional<UserEntity> dbUser = userRepository.findByEmail("nonexistent@email.com");

        assertThat(dbUser).isEmpty();
    }
}