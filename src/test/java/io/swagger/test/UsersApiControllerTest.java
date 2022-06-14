package io.swagger.test;

import io.swagger.model.Role;
import io.swagger.model.User;
import io.swagger.repository.UserRepository;
import io.swagger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsersApiControllerTest {

    @Autowired
    private UserService userService;

    private User testuser1;
    private User testuser2;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        testuser1 = new User();
        testuser1.setUserId(1);
        testuser1.setFullname("Patrick Jane");
        testuser1.setUsername("pat");
        testuser1.setRoles(Arrays.asList(Role.ROLE_USER));
        testuser1.setPassword(passwordEncoder.encode("pat123"));

        testuser2 = new User();
        testuser2.setUserId(2);
        testuser2.setFullname("Thresa Lisbon");
        testuser2.setUsername("thr");
        testuser2.setRoles(Arrays.asList(Role.ROLE_ADMIN));
        testuser2.setPassword("thr345");
    }

    @Test
    void canRetriveUserByID() {
        User user = userService.getUserModelById(1);
        assertEquals(user.getUserId(), testuser1.getUserId());
    }

    @Test
    public void canCreateANewUser() {
        User user = userService.createUser("pat", "Patrick Jane", "pat123", 0);
        assertEquals(user.getUsername(), testuser1.getUsername());
    }
}