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

public class AccountsStepDefs extends BaseStepDefinations implements En {

    private static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbXJpc2giLCJhdXRoIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlhdCI6MTY1NTY2NjM4NCwiZXhwIjoxNjU1NjY5OTg0fQ.7C7I2xMVVxDvixMJY0s8b3UqyXCAT-WYgDZ1kDkJOUM";
    private static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjU1NjY3NTk0LCJleHAiOjE2NTYyNzIzOTR9.XI7nat8c9C1oxrLkFydif3C6qtdzIIg6OGoiRcjLr6E";
    private static final String INVALID_TOKEN = "invalidtoken";

    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<String> response;
    private HttpEntity<String> request;
    private Integer status;

    private String token = null;

    private UserService userService;

    public AccountsStepDefs() {
        Given("^I have an valid token for role \"([^\"]*)\" to access accounts$", (String role) -> {
            if (role.equals("admin")) {
                token = VALID_TOKEN_ADMIN;
            }
        });

        When("^I call get accounts by IBAN \"([^\"]*)\"$", (String iban) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban, HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });

        Then("^I receive a status code of (\\d+)$", (Integer code) -> {
            Assertions.assertEquals(code, status);
        });

        // Senario 2
        Given("^I have an valid token for role \"([^\"]*)\" to access transactions$", (String role) -> {
             if (role.equals("user")) {
                 token = VALID_TOKEN_USER;
             }
             else if (role.equals("admin")) {
                 token = VALID_TOKEN_ADMIN;
             }
        });

        When("^I call get transactions by IBAN \"([^\"]*)\"$", (String iban) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban + "/transactions", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });
        Given("^I have an invalid token for role \"([^\"]*)\" to access transactions$", (String arg0) -> {
            token = INVALID_TOKEN;
        });
    }
}
