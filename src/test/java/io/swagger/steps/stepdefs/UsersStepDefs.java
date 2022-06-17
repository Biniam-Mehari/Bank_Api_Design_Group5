package io.swagger.steps.stepdefs;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java8.En;
import io.swagger.service.UserService;
import io.swagger.steps.BaseStepDefinations;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class UsersStepDefs extends BaseStepDefinations implements En {

    private static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbXJpc2giLCJhdXRoIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlhdCI6MTY1NTQ5NDA3MCwiZXhwIjoxNjU1NDk3NjcwfQ.4GrULuO29SO6WsLQj9alXk5s10Tjn_ZEfRDdXa_e47o";
    private static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiaW5pYW0iLCJhdXRoIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifSx7ImF1dGhvcml0eSI6IlJPTEVfQURNSU4ifV0sImlhdCI6MTY1NTQ5MjkxMCwiZXhwIjoxNjU1NDk2NTEwfQ.hA0knzD3ftwc3gHuB_p603SWUbxMpNJZg3-4ZKQoBrY";
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

        Given("^I have an invalid token for role \"([^\"]*)\"$", (String token) -> {
            token = INVALID_TOKEN;
        });

        When("I call get all users endpoint", () -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/users/", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
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



        When("^I call get total balance of user by Id (\\d+)$", (Integer id) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/users/" + id + "/totalBalance", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });
        Given("^I have an valid token for role \"([^\"]*)\"$", (String role) -> {

            if (role.equals("admin"))
                token = VALID_TOKEN_ADMIN;
            else if (role.equals("user"))
                token = VALID_TOKEN_USER;
        });
        When("^I call correctly get all users endpoint$", () -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "/bankAPI/users", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });
        Given("^kvsdnvknsdv$", () -> {
            // isdhvjd
        });
        Given("^I am abhishek$", () -> {
            // hghjgh
        });
        When("^hjjlk$", () -> {
            //kjhkj
        });


    }
}
