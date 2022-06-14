package io.swagger.steps.stepdefs;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java8.En;
import io.swagger.service.UserService;
import io.swagger.steps.BaseStepDefinations;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class UsersStepDefs extends BaseStepDefinations implements En {

    private static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmhpc2hlayIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9XSwiaWF0IjoxNjU1MjEwNzYyLCJleHAiOjE2NTUyMTQzNjJ9.HyB9tt_RbxjTX72CwfJAGICDUCV26Ol76U_HjdhbV-U";
    private static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b21teSIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjU1MjAzNzQ2LCJleHAiOjE2NTUyMDczNDZ9.XF4pAHlbcqZyOpW9bLACPFnR4z_f8tatYtR1YSr3YNs";
    private static final String INVALID_TOKEN = "invalidtoken";

    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<String> response;
    private HttpEntity<String> request;
    private Integer status;
    
    private String token = null;

    private UserService userService;

    public UsersStepDefs() {

        Given("^I have a valid token for role \"([^\"]*)\"$", (String role) -> {
            if (role == "user") {
                token = VALID_TOKEN_USER;
            }
        });

        When("I call get all users endpoint", () -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/users", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });

        Then("^I recieve a status code of (\\d+)$", (Integer code) -> {
            Assertions.assertEquals(code, status);
        });

        Given("^When I get user by Id (\\d+)$", (Integer id) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/users/" + id, HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });
        Given("^I have an invalid token for role \"([^\"]*)\"$", (String token) -> {
            token = INVALID_TOKEN;
        });

    }
}
