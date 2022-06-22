package io.swagger.api;

import io.swagger.jwt.JwtTokenProvider;
import io.swagger.model.Role;
import io.swagger.model.User;
import io.swagger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

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
    private JwtTokenProvider jwtTokenProvider;


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
    void canRetrieveUserByID() {
        User user = userService.getUserModelById(1);
        assertEquals(user.getUserId(), testuser1.getUserId());
    }

    @Test
    public void canCreateANewUser() {
        User user = userService.createUser("pat", "Patrick Jane", "pat123", 0);
        assertEquals(user.getUsername(), testuser1.getUsername());
    }

    @Test
    public void checkIfUserGetsBackALoginTokenOrNot() {
        String token = jwtTokenProvider.createToken(testuser1.getUsername(), testuser1.getRoles());
        assertNotNull(token);
    }

    @Test
    public void canGetAllUsersByFiltering() {
        // maybe can use pageable here
        List<User> users = userService.getAllUsers(0, 10, 0);
        assertNotNull(users);
    }

    // test if i can post a new user
    @Test
    public void canPostANewUser() {
        User user = userService.createUser("john12", "john doe", "secret", 0);
        assertEquals(user.getUsername(), "john12");
    }

    // test if i can get user by username
    @Test
    public void canGetUserByUsername() {
        User user = userService.getUserByUsername("amrish");
        assertEquals(user.getUsername(), "amrish");
    }

    // test if i can get total balance of user
    @Test
    public void canGetTotalBalanceOfUser() {
        User user = userService.getUserByUsername("amrish");
        Double balance = userService.getUserTotalBalance(user);
        // check if balance is not null
        assertNotNull(balance);
    }
}