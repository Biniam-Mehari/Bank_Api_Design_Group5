package io.swagger.test;

import io.swagger.model.Account;
import io.swagger.model.AccountType;
import io.swagger.model.Role;
import io.swagger.model.User;
import io.swagger.repository.UserRepository;
import io.swagger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration("/application-context.xml")
class UsersApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private User user;
    private User admin;
    private Account currentAccount;
    private Account savingAccount;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUserId(1);
        user.setFullname("Patrick Jane");
        user.setUsername("pat");
        user.setRoles(Arrays.asList(Role.ROLE_USER));
        user.setPassword(passwordEncoder.encode("pat123"));

        admin = new User();
        admin.setUserId(2);
        admin.setFullname("Thresa Lisbon");
        admin.setUsername("thr");
        admin.setRoles(Arrays.asList(Role.ROLE_ADMIN));
        admin.setPassword("lis123");

        currentAccount = new Account("NL31INHO0126400051", user, 20000.00, AccountType.current);
        savingAccount = new Account("NL41INHO0226400031", user, 1300.00, AccountType.saving);

    }

    @Test
    @WithMockUser(username = "thr", password = "lis123", roles = "ADMIN")
    public void callingAllUsersWithAdminRightsShouldReturnOK() throws Exception {
        given(userService.getAllUsers(0, 10, 0)).willReturn(Arrays.asList(user));
        mockMvc.perform(get("/bankAPI/users")).andExpect(status().isOk());
    }

    /*
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
    */

}