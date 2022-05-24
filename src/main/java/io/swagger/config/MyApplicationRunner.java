package io.swagger.config;

import io.swagger.model.Account;
import io.swagger.model.Role;
import io.swagger.model.User;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.UserRepository;
import io.swagger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MyWebSecurityConfig securityConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User firstUser = new User();
        firstUser.setUsername("biniam12");
        firstUser.setFullname("biniam mehari");
        firstUser.setPassword(securityConfig.passwordEncoder().encode("secret"));
        firstUser.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER, Role.ROLE_ADMIN)));

        User user2 = new User();
        user2.setUsername("tommy12");
        user2.setFullname("tommy king");
        user2.setPassword(securityConfig.passwordEncoder().encode("secret"));
        user2.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER)));



        // create a list of user
        List<User> users = new ArrayList<>(Arrays.asList(firstUser, user2));
        //List<User> users = List.of(firstUser);
        userRepository.saveAll(users);
        // create 100 dummy users for testing
        userService.create100RandomUsers();

        // Create an account for the BANK at system startup
        List<Account> accounts = List.of(new Account("NL01INHO0000000001",new User(), Double.MAX_VALUE, "bank"));
        accountRepository.saveAll(accounts);
    }

}
