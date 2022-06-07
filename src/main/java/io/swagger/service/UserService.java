package io.swagger.service;

import io.swagger.config.MyWebSecurityConfig;
import io.swagger.jwt.JwtTokenProvider;
import io.swagger.model.Role;
import io.swagger.model.User;
import io.swagger.model.dto.UserDTO;
import io.swagger.model.dto.UserResponseDTO;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private MyWebSecurityConfig securityConfig;

    public String login(String username, String password) {
        String token = "";

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            User user = userRepository.findByUsername(username);
            token = jwtTokenProvider.createToken(username, user.getRoles());
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username or password is incorrect");
        }
        return token;
    }

    public UserResponseDTO addExternalUser(UserDTO userDTO) {
        User userExists = userRepository.findByUsername(userDTO.getUsername());
        if (userExists != null) {
            return null;
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setFullname(userDTO.getFullname());
        user.setPassword(securityConfig.passwordEncoder().encode(userDTO.getPassword()));
        if (userDTO.getCreateEmployee() == 1){
            // Only admin can set createEmployee property to 1 in the UI
            user.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER, Role.ROLE_ADMIN)));
        }
        else {
            user.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER)));
        }
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        User newUser = userRepository.save(user);
        userResponseDTO.setUser(newUser);
        return userResponseDTO;
    }

    // this method is only used on startup to create 100 dummy users
    public User add(User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword())); //encrypt password
        return userRepository.save(user); // saves and returns a user
    }

    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUser(user);
        return userResponseDTO;
    }

    public User getUserModelById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getAllUsers(Integer skip, Integer limit, Integer withoutAccount) {
        // get all users without an account and skip and limit
        if (withoutAccount == 1) {
            // find users with no connected account
            return userRepository.findAllByAccountsIsNull();
        }
        return userRepository.findAllByUserIdAfterAndUserIdIsBefore(skip, (skip + limit + 1));
    }

    public List<UserResponseDTO> convertUsersToUserResponseDTO(List<User> users) {
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setUser(user);
            userResponseDTOs.add(userResponseDTO);
        }
        return userResponseDTOs;
    }

    public void create50RandomUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            User user = new User();
            String fullname = randomNameGenerator();
            user.setUsername(fullname + i);
            user.setFullname(fullname);
            user.setPassword(passwordEncoder.encode("secret"));
            user.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER)));
            users.add(user);
        }
        userRepository.saveAll(users);
    }

    public Double getUserTotalBalance(User user) {
        return accountRepository.getSumOfAllAccounts(user);
    }

    public String randomNameGenerator() {
        // create a random name
        String name = "";
        for (int i = 0; i < 10; i++) {
            name += (char) (Math.random() * 26 + 'a');
        }
        return name;
    }

    public User createUser(String username, String fullname, String password, Integer roles) {
        User user = new User();
        user.setUsername(username);
        user.setFullname(fullname);
        user.setPassword(passwordEncoder.encode(password));
        // if role 1, create employee else create customer
        if (roles == 1) {
            user.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER, Role.ROLE_ADMIN)));
        } else {
            user.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_USER)));
        }
        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updateUser(User user){
        userRepository.save(user);
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

}