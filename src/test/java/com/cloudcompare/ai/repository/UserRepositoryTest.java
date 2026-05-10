package com.cloudcompare.ai.repository;

import com.cloudcompare.ai.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFind() {
        UserEntity user = new UserEntity();
        user.setName("Repo Test");
        user.setEmail("repo@test.com");
        user.setPassword("hashed");
        
        userRepository.save(user);
        
        Optional<UserEntity> found = userRepository.findByEmail("repo@test.com");
        assertTrue(found.isPresent());
        assertEquals("Repo Test", found.get().getName());
    }
}
